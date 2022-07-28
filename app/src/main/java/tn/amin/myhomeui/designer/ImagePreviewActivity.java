package tn.amin.myhomeui.designer;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.Transition;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import tn.amin.myhomeui.R;
import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.designer.other.ViewFreezerOverlay;
import tn.amin.myhomeui.serializer.ViewDeserializerLayout;
import tn.amin.myhomeui.storage.preference.CustomizationBundle;
import tn.amin.myhomeui.util.LogUtil;


public class ImagePreviewActivity extends AppCompatActivity {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        Drawable wallpaper = StorageManager.tryLoadWallpaper();

        ViewDeserializerLayout previewLayout = findViewById(R.id.fullscreen_preview);
        previewLayout.setBackground(wallpaper);
        CustomizationBundle bundle = (CustomizationBundle) getIntent().getSerializableExtra("customization_bundle");
        if (bundle != null) {
            previewLayout.render(bundle.insertedItems);

            StorageManager.gatherLockscreenItems(this, (view) -> {
                LogUtil.debug("Callback " + view.getTag());

                if (!(view.getTag() instanceof String &&
                        bundle.hiddenItems.contains((String) view.getTag()))) {

                    LogUtil.debug("Rendering " + view.getTag());
                    previewLayout.addView(view);
                }
            }, false);
        } else {
            LogUtil.warn("Received a null bundle while displaying preview");
        }

        ViewFreezerOverlay overlay = findViewById(R.id.fullscreen_preview_overlay);
        overlay.show();

        getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) { }

            @Override
            public void onTransitionCancel(Transition transition) { }

            @Override
            public void onTransitionPause(Transition transition) { }

            @Override
            public void onTransitionResume(Transition transition) { }

            @Override
            public void onTransitionEnd(Transition transition) {
                overlay.hide();

                mHandler.postDelayed(() -> {
                    // Hide navigation bar
                    Window window = getWindow();
                    WindowInsetsControllerCompat windowInsetsController = WindowCompat.getInsetsController(window, window.getDecorView());
                    if (windowInsetsController != null) {
                        windowInsetsController.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars());
                    }
                }, 50);
            }
        });
    }

    private static void disableShowHideAnimation(ActionBar actionBar) {
        try
        {
            actionBar.getClass().getDeclaredMethod("setShowHideAnimationEnabled", boolean.class).invoke(actionBar, false);
        }
        catch (Exception exception)
        {
            try {
                Field mActionBarField = actionBar.getClass().getSuperclass().getDeclaredField("mActionBar");
                mActionBarField.setAccessible(true);
                Object icsActionBar = mActionBarField.get(actionBar);
                Field mShowHideAnimationEnabledField = icsActionBar.getClass().getDeclaredField("mShowHideAnimationEnabled");
                mShowHideAnimationEnabledField.setAccessible(true);
                mShowHideAnimationEnabledField.set(icsActionBar,false);
                Field mCurrentShowAnimField = icsActionBar.getClass().getDeclaredField("mCurrentShowAnim");
                mCurrentShowAnimField.setAccessible(true);
                mCurrentShowAnimField.set(icsActionBar,null);
            } catch (Exception e){
                LogUtil.warn("Could not hide actionBar");
            }
        }
    }
}