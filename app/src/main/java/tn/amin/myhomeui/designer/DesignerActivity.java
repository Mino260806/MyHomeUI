package tn.amin.myhomeui.designer;

import static tn.amin.myhomeui.storage.StorageManager.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.documentfile.provider.DocumentFile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.sothree.slidinguppanel.PanelState;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import eltos.simpledialogfragment.SimpleDialog;
import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.storage.communicator.Communicator;
import tn.amin.myhomeui.storage.communicator.MessageType;
import tn.amin.myhomeui.storage.preference.CustomizationBundle;
import tn.amin.myhomeui.storage.preference.SharedPreferenceManager;
import tn.amin.myhomeui.designer.previewframe.PreviewFrame;
import tn.amin.myhomeui.designer.previewframe.PreviewFrameContainer;
import tn.amin.myhomeui.designer.toolbar.ToolBar;
import tn.amin.myhomeui.util.LogUtil;
import tn.amin.myhomeui.R;

@SuppressLint("WorldReadableFiles")
public class DesignerActivity extends AppCompatActivity implements SimpleDialog.OnDialogResultListener, PreviewFrame.TutorialListener {
    private SharedPreferenceManager pref;
    private PreviewFrame previewFrame;
    private PreviewFrameContainer previewFrameContainer;
    private LinearProgressIndicator progressIndicator;
    private ConstraintLayout designerInsertionBox;
    private ToolBar toolBar;
    private SlidingUpPanelLayout slidingPanel;

    public static AppWidgetManager appWidgetManager;
    public static AppWidgetHost appWidgetHost;

    private ActivityResultLauncher<Intent> designerActivityResultLauncher;
    private ActivityResultLauncher<Intent> editImageResultLauncher;
    private ActivityResultLauncher<Intent> queryImageResultLauncher;
    private ActivityResultLauncher<Intent> queryFontResultLauncher;

    private Consumer<File> fontConsumer = (fileName -> {});
    private HashMap<String, SimpleDialog.OnDialogResultListener> dialogResultListeners = new HashMap<>();

    private SharedPreferences sharedPreferences;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private long lockscreenItemsLastModified = -1;
    private boolean isModuleEnabled;
    private AlertDialog mNeedsDismissDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        LogUtil.setContext(this);
        ensureModuleIsEnabled();
        if (!isModuleEnabled) {
            return;
        }

        setContentView(R.layout.activity_designer);

        pref = SharedPreferenceManager.getInstance(this);
        sharedPreferences = getPreferences(MODE_PRIVATE);

        mHandler.post(() -> StorageManager.removeRedundantFiles(pref.lockscreen().getInsertedItems()));

        appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetHost = new AppWidgetHost(this, R.id.appwidget_host_id);

        designerInsertionBox = findViewById(R.id.designer_insertion_box);
        previewFrameContainer = findViewById(R.id.designer_preview_container);
        previewFrame = previewFrameContainer.previewFrame;
        previewFrame.setTutorialListener(this);
        toolBar = findViewById(R.id.designer_toolbar);
        slidingPanel = findViewById(R.id.sliding_panel);

        previewFrame.setToolBar(toolBar);
        reloadLockscreen();

        slidingPanel.setTouchEnabled(lockscreenPreviewExists());

        Button insertTextButton = findViewById(R.id.designer_add_text);
        Button insertImageButton = findViewById(R.id.designer_add_image);
        insertTextButton.setOnClickListener((v) -> previewFrame.insertText());
        insertImageButton.setOnClickListener((v) -> queryImage());

        designerActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> { });

        editImageResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null && result.getData().getData() != null) {
                        Uri uri = result.getData().getData();
                    }
                });

        queryImageResultLauncher = getResultLauncherForMimeType("image/*",
                (file) -> previewFrame.insertImage(file));
        queryFontResultLauncher = getResultLauncherForMimeType("font/*",
                (file) -> fontConsumer.accept(file));

        animateOnLaunch();
        refreshLastModified();
    }

    private void refreshLastModified() {
        lockscreenItemsLastModified = lockscreenItemsDir.lastModified();
    }

    private void ensureModuleIsEnabled() {
        isModuleEnabled = Communicator.isModuleEnabled();
        if (!isModuleEnabled) {
            new AlertDialog.Builder(this)
                    .setTitle("Module Disabled")
                    .setMessage("Steps to follow to activate it:\n" +
                            "- Enable module in LSPosed\n" +
                            "- Reboot device")
                    .setCancelable(false)
                    .setNeutralButton(android.R.string.ok, (dialog, which) -> {
                        DesignerActivity.this.finish();
                    })
                    .create()
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if (isModuleEnabled && !lockscreenPreviewExists()) {
            mHandler.post(() -> {
                final View refreshButton = findViewById(R.id.menu_action_refresh);
                TapTargetView.showFor(this,
                    TapTarget.forView(refreshButton, "Refresh Preview",
                            "In order to have a preview of your lockscreen, " +
                                    "you must click this button and follow the instructions carefully")
                            .cancelable(false),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            refreshButton.performClick();
                        }
                    });
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_action_save) {
            save();
        } else if (item.getItemId() == R.id.menu_action_refresh) {
            preRefreshLockscreen();
        } else if (item.getItemId() == R.id.menu_action_preview) {
            if (toolBar.isShown() || slidingPanel.getPanelState() != PanelState.COLLAPSED) {
                toolBar.hideFormatting();
                slidingPanel.setPanelState(PanelState.COLLAPSED);
                mHandler.postDelayed(this::showPreview, 250);
            }
            else showPreview();
        } else if (item.getItemId() == R.id.menu_action_restore) {
            pref.lockscreen().setHiddenLockscreenItems(Collections.emptySet());
            reloadLockscreen();
        } else if (item.getItemId() == R.id.menu_action_settings) {
            startActivity(new Intent(DesignerActivity.this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void animateOnLaunch() {
        previewFrameContainer.previewFrame.setAlpha(0f);
        previewFrameContainer.background.setAlpha(0f);
        previewFrameContainer.background
                .animate()
                .alpha(1f)
                .setDuration(200)
                .withEndAction(() -> {
                    previewFrameContainer.previewFrame.animate()
                            .alpha(1f)
                            .setDuration(600)
                            .start();
                })
                .start();
    }

    public void save() {
        CustomizationBundle bundle = previewFrame.putInBundle();
        new Thread(() -> {
            pref.lockscreen().setBundle(bundle);
            Communicator.sendMessage(MessageType.RENDER);
        }).start();
    }

    public void preRefreshLockscreen() {
        AlertDialog.Builder builder = getDontShowAgainDialog("refresh_instructions");
        Communicator.sendMessage(MessageType.REFRESH, "true");
        if (builder != null) {
            mNeedsDismissDialog = builder.setTitle("Refresh Preview")
                    .setCancelable(false)
                    .setMessage("Please turn off the screen and turn it on without unlocking it. " +
                            "Wait around 3 seconds and then unlock it again. ")
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        Communicator.sendMessage(MessageType.REFRESH, "false");
                    })
                    .show();
        }
    }

    public void showPreview() {
        findViewById(android.R.id.content).clearFocus();
        StorageManager.savePreviewImage(previewFrame);
        Intent intent = new Intent(DesignerActivity.this, ImagePreviewActivity.class);
        intent.putExtra("customization_bundle", previewFrame.putInBundle());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, previewFrame, previewFrame.getTransitionName());
        designerActivityResultLauncher.launch(intent, options);
    }

    public void queryImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        queryImageResultLauncher.launch(intent);
    }

    public void queryFont(Consumer<File> callback) {
        fontConsumer = callback;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("font/*");
        queryFontResultLauncher.launch(intent);
    }

    public void queryWidget() {
        // DOESNT WORK
        int appWidgetId = appWidgetHost.allocateAppWidgetId();
//        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
//        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        ArrayList<Parcelable> customInfo = new ArrayList<>();
//        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
//        ArrayList<Parcelable> customExtras = new ArrayList<>();
//        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
//        startActivity(pickIntent);

        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, );
// This is the options bundle described in the preceding section.
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS, options);
        startActivity(intent);
    }

    public Thread indicateProgress(Runnable action, Runnable onFinish) {
        if (progressIndicator == null) {
            progressIndicator = findViewById(R.id.linear_progress_bar);
            progressIndicator.setVisibilityAfterHide(View.GONE);
        }
        progressIndicator.show();
        Thread thread = new Thread(() -> {
            action.run();
            runOnUiThread(() -> {
                progressIndicator.hide();
                onFinish.run();
            });
        });
        thread.start();
        return thread;
    }

    private ActivityResultLauncher<Intent> getResultLauncherForMimeType(String mimeType, Consumer<File> callback) {
        return registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getData() != null && result.getData().getData() != null) {
                        Uri uri = result.getData().getData();
                        File savedFile;
                        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                            ContentResolver cR = getContentResolver();
                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                            String extension = mime.getExtensionFromMimeType(cR.getType(uri));

                            DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
                            String fileName;
                            if (documentFile != null) fileName = documentFile.getName();
                            else fileName = UUID.randomUUID().toString() + "." + extension;
                            savedFile = saveFile(inputStream, fileName, mimeType.split("/")[0]);
                            if (savedFile == null) throw new IOException();
                        } catch (IOException e) {
                            LogUtil.error("Failed to query " + mimeType, e);
                            return;
                        }
                        callback.accept(savedFile);
                    }
                });
    }


    public void addDialogListener(String tag, SimpleDialog.OnDialogResultListener listener) {
        dialogResultListeners.put(tag, listener);
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        return dialogResultListeners.getOrDefault(dialogTag, (d, w, e)  -> false).onResult(dialogTag, which, extras);
    }

    private AlertDialog.Builder getDontShowAgainDialog(String id) {
        if (sharedPreferences.getBoolean("hide_dialog_" + id, false))
            return null;

        return new AlertDialog.Builder(this)
                .setMultiChoiceItems(
                        new CharSequence[] { "Don't show again" },
                        null,
                        (dialog, which, isChecked) -> {
                            sharedPreferences.edit().putBoolean("hide_dialog_" + id, isChecked).apply();
                        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pref = SharedPreferenceManager.getInstance(this);

        long oldLastModified = lockscreenItemsLastModified;
        refreshLastModified();
        if (oldLastModified != -1 && oldLastModified < lockscreenItemsLastModified) {
            if (mNeedsDismissDialog != null) mNeedsDismissDialog.hide();
            reloadLockscreen();
        }
    }

    public void reloadLockscreen() {
        if (previewFrame != null) previewFrame.setBundle(pref.lockscreen().getBundle());
        if (lockscreenPreviewExists()) {
            slidingPanel.setTouchEnabled(true);
            if (!pref.designer().knowsHowToInsert()) {
                TapTargetView.showFor(this,
                    TapTarget.forView(findViewById(R.id.slide_panel_drag_view),
                                    "Insert an Item",
                                    "To insert an item, click / pull up the insertion box right here")
                            .cancelable(false)
                            .tintTarget(false),
                    new TapTargetView.Listener() {
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);
                            pref.designer().setKnowsHowToInsert(true);
                            slidingPanel.setPanelState(PanelState.EXPANDED);
                        }
                    });
            }
        }
    }

    @Override
    public void onFinished() {
        new AlertDialog.Builder(this)
                .setTitle("Tutorial Finished")
                .setMessage("This is it for now. Unleash your imagination and customize your lockscreen!\n\n" +
                        "If you have some suggestions, or you encounter a problem, you can always go to Settings > Contact developer")
                .setPositiveButton("Alright! Got it", (dialog, which) -> {})
                .show();
    }
}