package tn.amin.myhomeui.lockscreen.features;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.myhomeui.MyHomeHook;
import tn.amin.myhomeui.util.DisplayUtil;
import tn.amin.myhomeui.util.LogUtil;

public class ShowNotifOnLongPressFeature extends BaseFeature implements View.OnTouchListener {
    private Handler mHandler;
    private boolean hooksReady;

    @Override
    public void init() {
        mHandler = MyHomeHook.getInstance().handler;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!hooksReady) initHooks();
    }

    private void initHooks() {
        hooksReady = true;
//        Class<?> NotificationLockscreenUserManagerImpl = XposedHelpers.findClass("com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl", MyHomeHook.getInstance().cl);
//        XposedBridge.hookAllMethods(NotificationLockscreenUserManagerImpl, "shouldShowOnKeyguard", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                LogUtil.debug("shouldShowLockscreenNotifications called");
//                param.setResult(true);
//            }
//        });
    }

    @Override
    public String getPrefName() {
        return "pref_show_notif";
    }

    @Override
    public String[] getNeededViewIds() {
        return new String[] {};
    }

    @Override
    public void onViewFound(String id, View view) {
    }

    enum NotifState {
        HIDDEN,
        HIDING,
        SHOWN,
        SHOWING
    }
}
