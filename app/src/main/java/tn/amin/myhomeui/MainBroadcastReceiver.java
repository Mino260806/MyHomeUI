package tn.amin.myhomeui;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tn.amin.myhomeui.lockscreen.LockscreenObserver;
import tn.amin.myhomeui.util.LogUtil;

public class MainBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.debug("New Action: " + intent.getAction());
        switch (intent.getAction()) {
            case Intent.ACTION_USER_PRESENT:
                KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE);
                if (!keyguardManager.isKeyguardLocked()) {
                    LockscreenObserver.getInstance().notifyDeviceUnlocked();
                }
                break;
            case Intent.ACTION_SCREEN_OFF:
                LockscreenObserver.getInstance().notifyDeviceLocked();
                break;
            case Intent.ACTION_SCREEN_ON:
                LockscreenObserver.getInstance().notifyScreenOn();
        }
    }
}