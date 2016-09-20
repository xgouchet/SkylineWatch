package fr.xgouchet.skylinewatch;

import android.app.Application;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * @author Xavier Gouchet
 */
public class SkylineApplication extends Application {

    @NonNull
    private List<SkylineScreen> screens;

    @Override
    public void onCreate() {
        super.onCreate();

        screens = SkylinePreferences.getScreens(this);
        if (screens.isEmpty()) {
            screens.add(new SkylineScreen(null, R.drawable.sl_miami, 0));
            SkylinePreferences.saveScreens(screens, this);
        }
    }

    @NonNull
    public List<SkylineScreen> getScreens() {
        return screens;
    }
}
