package tn.amin.myhomeui.lockscreen.features;

import android.view.View;

public interface Feature {
    boolean isEnabled();
    void setEnabled(boolean enabled);
    void setEnabled(boolean enabled, boolean lock);
    void unlockAndRefresh();
    String getPrefName();
    String[] getNeededViewIds();
    void onViewFound(String id, View view);
    void init();
    void refresh();
}
