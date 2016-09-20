package fr.xgouchet.skylinewatch.ui;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.View;

import fr.xgouchet.skylinewatch.R;

/**
 * @author Xavier Gouchet
 */
public class ConfigFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.skyline_config);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Preference screensPreferences = findPreference("screens-dummy");
        if (screensPreferences != null) {
            screensPreferences.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.fragment, new ScreenFragment())
                            .commit();
                    return true;
                }
            });
        }
    }
}
