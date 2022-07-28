package tn.amin.myhomeui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import tn.amin.myhomeui.lockscreen.LockscreenObserver;
import tn.amin.myhomeui.lockscreen.RelativeView;
import tn.amin.myhomeui.lockscreen.TouchInterceptorView;
import tn.amin.myhomeui.lockscreen.features.DisableStatusBarFeature;
import tn.amin.myhomeui.lockscreen.features.Feature;
import tn.amin.myhomeui.lockscreen.features.KonfettiFeature;
import tn.amin.myhomeui.lockscreen.features.ShowNotifOnLongPressFeature;
import tn.amin.myhomeui.serializer.ViewDeserializerLayout;
import tn.amin.myhomeui.storage.communicator.Communicator;
import tn.amin.myhomeui.storage.communicator.MessageType;
import tn.amin.myhomeui.storage.preference.SharedPreferenceManager;
import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.util.LogUtil;
import tn.amin.myhomeui.util.ViewUtil;

public class MyHomeHook implements IXposedHookLoadPackage, View.OnTouchListener, Observer {
    public Application systemUIApplication;
    public ViewGroup root;
    public FrameLayout bottomAreaView;
    public FrameLayout panelView;
    public ViewGroup notifContainer;

    private ConstraintLayout drawArea;
    private TouchInterceptorView touchInterceptorView;
    private ViewDeserializerLayout customizerLayout;
    private KonfettiView konfettiView;
    private RelativeView refreshButton;
    private View statusBar;

    private SharedPreferenceManager pref = null;
    public ClassLoader cl;
    public Handler handler;

    private MainBroadcastReceiver receiver;

    private final static int FEATURE_DISABLE_STATUSBAR = 0;
    private final static int FEATURE_SHOW_NOTIF = 1;
    private final static int FEATURE_KONFETTI = 2;
    private Feature[] features = new Feature[] {
            new DisableStatusBarFeature(),
            new ShowNotifOnLongPressFeature(),
            new KonfettiFeature()
    };

    private ArrayList<View.OnTouchListener> touchListeners = new ArrayList<>();
    private boolean needsRefresh = false;
    private boolean initialized = false;
    private boolean deviceWasUnlocked = false;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!Constants.TARGET_PACKAGE.equals(lpparam.packageName))
            return;
        cl = lpparam.classLoader;

        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.KeyguardBottomAreaView", lpparam.classLoader,
                "onFinishInflate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        bottomAreaView = (FrameLayout) param.thisObject;
                    }
                });

        XposedHelpers.findAndHookMethod("com.android.systemui.keyguard.KeyguardService", lpparam.classLoader,
                "onCreate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Service service = (Service) param.thisObject;
                        systemUIApplication = service.getApplication();
                        handler = new Handler(Looper.getMainLooper());

                        setupReceivers();
                    }
                });
    }

    @SuppressLint({"MissingPermission", "ClickableViewAccessibility"})
    private void initLockscreenCustomization() {
        initialized = true;
        LogUtil.debug("Initializing now...");

        pref = SharedPreferenceManager.getInstance(systemUIApplication);
        if (!pref.canRead())
            LogUtil.debug("Failed to load config, ignoring...");

        root = (ViewGroup) bottomAreaView.getRootView();

        // Make BottomAreaView expand across all the screen
        // Now we are able to place stuff freely !
        panelView = (FrameLayout) bottomAreaView.getParent();
        int oldIndex = panelView.indexOfChild(bottomAreaView);
        ViewGroup.LayoutParams params = bottomAreaView.getLayoutParams();
        panelView.removeView(bottomAreaView);
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        bottomAreaView.setTranslationZ(Float.MIN_VALUE);
        panelView.addView(bottomAreaView, oldIndex, params);

        drawArea = new ConstraintLayout(root.getContext());
        drawArea.setTag(Constants.LOCKSCREEN_VIEW_TAG);
        bottomAreaView.addView(drawArea, params);
        params = drawArea.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        drawArea.requestLayout();

        for (Feature f: features) {
            for (String id: f.getNeededViewIds()) {
                f.onViewFound(id, ViewUtil.findViewByStringId(root, id));
            }
            f.init();
        }

        // This will automatically include all the customizations :)
        customizerLayout = new ViewDeserializerLayout(root.getContext());
        customizerLayout.setTranslationZ(1);
        params = new ConstraintLayout.LayoutParams(params);
        drawArea.addView(customizerLayout, params);
        renderLockscreenCustomization();

        for (Feature f: features) f.refresh();

        setupCommunication();
    }

    private void setupCommunication() {
        Communicator.startListening((message) -> {
            LogUtil.debug("New Message: " + message);
            switch (message.type) {
                case RENDER:
                    handler.post(this::renderLockscreenCustomization);
                    break;

                case REFRESH:
                    needsRefresh = Boolean.parseBoolean(message.content);
                    break;

                case PREF_CHANGED:
                    for (Feature f: features) {
                        if (f.getPrefName().equals(message.content)) {
                            f.refresh();
                        }
                    }
            }
        });
    }

    private void setupReceivers() {
        LockscreenObserver.getInstance().addObserver(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        receiver = new MainBroadcastReceiver();
        systemUIApplication.registerReceiver(receiver, filter);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof LockscreenObserver) {
            LogUtil.debug("Received event: " + arg);
            switch ((String) arg) {
                case LockscreenObserver.RECEIVER_DEVICE_UNLOCKED:
                    if (!deviceWasUnlocked) {
                        deviceWasUnlocked = true;
                        handler.postDelayed(Communicator::validateModuleIsEnabled, 3000);
                    }
                    if (initialized) {
                        features[FEATURE_SHOW_NOTIF].setEnabled(false, true);
                        features[FEATURE_KONFETTI].setEnabled(false, true);
                        features[FEATURE_DISABLE_STATUSBAR].setEnabled(false, true);
                    }
                    break;
                case LockscreenObserver.RECEIVER_DEVICE_LOCKED:
                    if (initialized) {
                        handler.post(() -> {
                            features[FEATURE_DISABLE_STATUSBAR].unlockAndRefresh();
                            features[FEATURE_SHOW_NOTIF].unlockAndRefresh();
                        });
                    }
                    break;
                case LockscreenObserver.RECEIVER_SCREEN_ON:
                    if (deviceWasUnlocked && !initialized) initLockscreenCustomization();
                    if (needsRefresh) {
                        needsRefresh = false;
                        new Thread(() -> {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ignored) {}
                            StorageManager.saveWallpaper(root, cl);
                            StorageManager.saveLockscreenItems(root, null);
                            handler.post(() -> {
                                LogUtil.debug("Finished refresh");
                                Toast.makeText(systemUIApplication, "Finished refresh", Toast.LENGTH_SHORT).show();
                                Communicator.sendMessage(MessageType.REFRESH_SUCCESS, "true");
                            });
                        }).start();
                    } else {

                    }
                    break;
            }
        }
    }

    Set<String> previouslyHidden = null;
    public void renderLockscreenCustomization() {
        pref.reload();
        HashSet<Map<String, Object>> insertedItems;
        Set<String> hiddenItems;
        try {
            insertedItems = pref.lockscreen().getInsertedItems(true);
            hiddenItems = pref.lockscreen().getHiddenLockscreenItems();
        } catch (RuntimeException e) {
            LogUtil.error("Failed to get customizations, ignoring...", e);
            return;
        }
        customizerLayout.render(insertedItems);
        for (String id: hiddenItems) {
            View v = ViewUtil.findViewByStringId(root, id);
            v.setVisibility(View.INVISIBLE);
            v.setAlpha(0);
            XposedHelpers.setAdditionalInstanceField(v, "hidden_by_user", true);
        }
        if (previouslyHidden != null) {
            previouslyHidden.removeAll(hiddenItems);
            for (String id: previouslyHidden) {
                View v = ViewUtil.findViewByStringId(root, id);
                v.setVisibility(View.VISIBLE);
                v.setAlpha(1);
                XposedHelpers.setAdditionalInstanceField(v, "hidden_by_user", false);
            }
        }
        previouslyHidden = hiddenItems;
    }

    private static WeakReference<MyHomeHook> instance = null;
    public MyHomeHook() {
        instance = new WeakReference<>(this);
    }

    public static MyHomeHook getInstance() {
        return instance.get();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return touchListeners.stream().anyMatch((listener) -> listener.onTouch(v, event));
    }

    public void addOnTouchListener(View.OnTouchListener listener) {
        touchListeners.add(listener);
    }
}
