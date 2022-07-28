package tn.amin.myhomeui.lockscreen.features;

import android.annotation.SuppressLint;
import android.app.StatusBarManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.myhomeui.MyHomeHook;
import tn.amin.myhomeui.util.DisplayUtil;

public class DisableStatusBarFeature extends BaseFeature {
    private Object statusBarManager;

    public static final int DISABLE2_NONE = 0;
    public static final int DISABLE2_QUICK_SETTINGS = 1;
    public static final int DISABLE2_NOTIFICATION_SHADE = 1 << 2;

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled)
            XposedHelpers.callMethod(statusBarManager, "disable2", DISABLE2_QUICK_SETTINGS);
        else
            XposedHelpers.callMethod(statusBarManager, "disable2", DISABLE2_NONE);
    }

    @Override
    public String getPrefName() {
        return "pref_prevent_statusbar";
    }

    @SuppressLint("WrongConstant")
    @Override
    public void init() {
        statusBarManager = MyHomeHook.getInstance().systemUIApplication.getSystemService("statusbar");
    }
}
