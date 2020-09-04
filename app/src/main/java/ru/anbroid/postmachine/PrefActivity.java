package ru.anbroid.postmachine;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;

import androidx.preference.PreferenceManager;
import android.view.View;

/**
 * @author AnBro-ID, 2018
 * Активность настроек приложения
 */

public class PrefActivity extends AppCompatActivity
{
    private SharedPreferences sp;
    private SharedPreferences.OnSharedPreferenceChangeListener settingListener;

    /**
     * Компонент для вывода настроек
     */

    public static class SettingsFragment extends PreferenceFragmentCompat
    {
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.pref_general, rootKey);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pref);

        Toolbar toolbar = findViewById(R.id.toolbar_pref);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) { finish(); }
        });

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        settingListener = new SharedPreferences.OnSharedPreferenceChangeListener()
        {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
            {
                if (key.equals("speed_list"))
                    MainActivity.speed = Integer.parseInt(sharedPreferences.getString(key, "500"));
            }
        };

        sp.registerOnSharedPreferenceChangeListener(settingListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.pref_layout, new SettingsFragment()).commit();
    }

    protected void onResume()
    {
        super.onResume();
        sp.registerOnSharedPreferenceChangeListener(settingListener);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sp.unregisterOnSharedPreferenceChangeListener(settingListener);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        sp.unregisterOnSharedPreferenceChangeListener(settingListener);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        sp.unregisterOnSharedPreferenceChangeListener(settingListener);
    }
}
