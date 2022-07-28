package tn.amin.myhomeui.lockscreen.features;

import android.view.View;

import java.util.HashMap;

import tn.amin.myhomeui.storage.preference.SharedPreferenceManager;

abstract class BaseFeature implements Feature {
    protected boolean enabled = false;
    protected boolean locked = false;

    @Override
    public void setEnabled(boolean enabled, boolean lock) {
        setEnabled(enabled);
        this.locked = lock;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void refresh() {
        if (locked) return;
        setEnabled(isEnabled());
    }

    protected boolean isEnabledByDefault() {
        return false;
    }

    @Override
    public void unlockAndRefresh() {
        locked = false;
        refresh();
    }

    @Override
    public String[] getNeededViewIds() {
        return new String[0];
    }

    @Override
    public void onViewFound(String id, View view) {
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(SharedPreferenceManager.getInstance()
                .lockscreen().getBook().read(getPrefName(), isEnabledByDefault()));
    }
}
