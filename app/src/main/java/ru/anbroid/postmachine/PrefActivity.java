package ru.anbroid.postmachine;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

/**
 * @author AnBro-ID, 2018
 * Активность настроек приложения
 */

public class PrefActivity extends AppCompatActivity
{
    /**
     * Компонент для вывода настроек
     */

    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
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

        getFragmentManager().beginTransaction().replace(R.id.pref_layout, new SettingsFragment()).commit();
    }
}
