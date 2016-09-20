package fr.xgouchet.skylinewatch;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import preference.WearPreferenceActivity;

/**
 * @author Xavier Gouchet
 */
public class SkylineConfigActivity extends WearPreferenceActivity {


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.skyline_config);
    }

}
