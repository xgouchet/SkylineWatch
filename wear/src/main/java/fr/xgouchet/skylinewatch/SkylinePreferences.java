package fr.xgouchet.skylinewatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Xavier Gouchet
 */
public class SkylinePreferences {

    private static final String SCREENS = "screens";
    private static final String DISPLAY_AM_PM = "display_am_pm";
    private static final String DISPLAY_SECONDS = "display_seconds";
    private static final String DISPLAY_DATE = "display_date";

    @NonNull
    public static List<SkylineScreen> getScreens(@NonNull Context context) {
        final ArrayList<SkylineScreen> skylineScreens = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String screensJson = preferences.getString(SCREENS, null);

        if (screensJson != null) {
            try {
                JSONArray array = new JSONArray(screensJson);
                int count = array.length();

                for (int i = 0; i < count; ++i) {
                    skylineScreens.add(SkylineScreen.fromJson(array.getJSONObject(i)));
                }
            } catch (JSONException e) {
                preferences.edit().remove(SCREENS).apply();
                skylineScreens.clear();
            }
        }

        return skylineScreens;
    }

    public static void saveScreens(@NonNull List<SkylineScreen> skylineScreens, @NonNull Context context) {

        JSONArray array = new JSONArray();
        for (SkylineScreen screen : skylineScreens) {
            try {
                array.put(screen.toJson());
            } catch (JSONException e) {
                // ignore ?
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(SCREENS, array.toString()).apply();
    }


    public static boolean shouldDisplaySeconds(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(DISPLAY_SECONDS, false);
    }

    public static boolean shouldDisplayDate(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(DISPLAY_DATE, true);
    }


    public static boolean shouldDisplayAmPm(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(DISPLAY_AM_PM, false);
    }
}
