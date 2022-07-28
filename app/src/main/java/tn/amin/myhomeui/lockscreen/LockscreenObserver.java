package tn.amin.myhomeui.lockscreen;

import java.util.Observable;

public class LockscreenObserver extends Observable {
    public static final String RECEIVER_DEVICE_UNLOCKED = "device_unlocked";
    public static final String RECEIVER_DEVICE_LOCKED = "device_locked";
    public static final String RECEIVER_SCREEN_ON = "screen_on";

    private static LockscreenObserver instance = new LockscreenObserver();

    public static LockscreenObserver getInstance() {
        return instance;
    }

    private LockscreenObserver() {
    }

    public void notifyDeviceUnlocked() {
        notifyAbout(RECEIVER_DEVICE_UNLOCKED);
    }

    public void notifyDeviceLocked() {
        notifyAbout(RECEIVER_DEVICE_LOCKED);
    }

    public void notifyScreenOn() {
        notifyAbout(RECEIVER_SCREEN_ON);
    }

    public void notifyAbout(Object data) {
        synchronized (this) {
            setChanged();
            notifyObservers(data);
        }
    }
}
