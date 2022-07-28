package tn.amin.myhomeui.designer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.CheckBoxPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.ListAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import io.paperdb.Book;
import io.paperdb.Paper;
import tn.amin.myhomeui.Constants;
import tn.amin.myhomeui.R;
import tn.amin.myhomeui.storage.StorageManager;
import tn.amin.myhomeui.storage.communicator.Communicator;
import tn.amin.myhomeui.storage.communicator.MessageType;
import tn.amin.myhomeui.storage.preference.LockscreenSettingsManager;
import tn.amin.myhomeui.storage.preference.SharedPreferenceManager;
import tn.amin.myhomeui.util.LogUtil;


/*
* Adapted from https://stackoverflow.com/a/32540395/10231266
* */
public class SettingsActivity extends AppCompatActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferenceManager.getInstance(this);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            // Create the fragment only when the activity is created for the first time.
            // ie. not after orientation changes
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(SettingsFragment.FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new SettingsFragment();
            }

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.settings_fragment_container, fragment, SettingsFragment.FRAGMENT_TAG);
            ft.commit();
        }
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat preferenceFragmentCompat,
                                           PreferenceScreen preferenceScreen) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey());
        fragment.setArguments(args);
        ft.replace(R.id.settings_fragment_container, fragment, preferenceScreen.getKey());
        ft.addToBackStack(preferenceScreen.getKey());
        ft.commit();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        final static String FRAGMENT_TAG = "SETTINGS_FRAGMENT";

        private Book book = SharedPreferenceManager.getInstance().lockscreen().getBook();

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            setPreferencesFromResource(R.xml.preference_settings, rootKey);

            for (Preference preference: getPreferenceList(getPreferenceScreen(), new ArrayList<>())) {
                preference.setOnPreferenceChangeListener(this);
            }
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            String key = preference.getKey();

            if ("pref_refresh_button".equals(key))
                newValue = Integer.valueOf(newValue.toString(), 2);

            book.write(key, newValue);

            Communicator.sendMessage(MessageType.PREF_CHANGED, key);

            return true;
        }

        @Override
        public boolean onPreferenceTreeClick(@NonNull Preference preference) {
            if ("pref_report_problem".equals(preference.getKey())) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{ Constants.DEV_MAIL });
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + ": suggestion / problem report");
                i.putExtra(Intent.EXTRA_TEXT   ,
                        "Android Sdk Version: " + Build.VERSION.SDK_INT + "\n" +
                        "Device: " + Build.MANUFACTURER + " " + Build.BRAND + " " + Build.MODEL + "\n\n" +
                        "Describe your problem / suggestion :\n");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
            return false;
        }

        private ArrayList<Preference> getPreferenceList(Preference p, ArrayList<Preference> list) {
            if( p instanceof PreferenceCategory || p instanceof PreferenceScreen) {
                PreferenceGroup pGroup = (PreferenceGroup) p;
                int pCount = pGroup.getPreferenceCount();
                for(int i = 0; i < pCount; i++) {
                    getPreferenceList(pGroup.getPreference(i), list); // recursive call
                }
            } else {
                list.add(p);
            }
            return list;
        }
    }
}
