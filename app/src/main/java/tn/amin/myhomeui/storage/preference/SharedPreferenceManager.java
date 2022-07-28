package tn.amin.myhomeui.storage.preference;

import android.content.Context;

import io.paperdb.Book;
import io.paperdb.Paper;
import tn.amin.myhomeui.storage.StorageManager;

public class SharedPreferenceManager {
    private static SharedPreferenceManager mInstance = null;

    public static SharedPreferenceManager getInstance() {
        if (mInstance == null)
            throw new NullPointerException("call SharedPreferenceManager.getInstance(Context) first.");
        return mInstance;
    }

    // Context is a dummy
    public static SharedPreferenceManager getInstance(Context context) {
        if (mInstance == null) mInstance = new SharedPreferenceManager(context);
        return mInstance;
    }

    private Book lockscreenBook;
    private Book designerBook;

    private DesignerSettingsManager designerSettingsManager;
    private LockscreenSettingsManager lockscreenSettingsManager;

    public SharedPreferenceManager(Context context) {
        Paper.init(context);

        lockscreenBook = Paper.bookOn(StorageManager.configDir.getAbsolutePath(), LockscreenSettingsManager.PREF_NAME);
        designerBook = Paper.bookOn(StorageManager.configDir.getAbsolutePath(), DesignerSettingsManager.PREF_NAME);
        initSettingsManagers();
    }

    private void initSettingsManagers() {
        designerSettingsManager = new DesignerSettingsManager(designerBook);
        lockscreenSettingsManager = new LockscreenSettingsManager(lockscreenBook);
    }

    public boolean canRead() {
//        if (lockscreenPref instanceof XSharedPreferences) return ((XSharedPreferences) lockscreenPref).getFile().canRead();
//        else return true;
        return true;
    }

    public void reload() {
//        if (lockscreenPref instanceof XSharedPreferences) ((XSharedPreferences) lockscreenPref).reload();
    }

    public DesignerSettingsManager designer() { return designerSettingsManager; }
    public LockscreenSettingsManager lockscreen() { return lockscreenSettingsManager; }

}
