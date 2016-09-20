package fr.xgouchet.skylinewatch;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

/**
 * @author Xavier Gouchet
 */
public class SkylineScreen {


    private static final String TIMEZONE = "timezone";
    private static final String DRAWABLE = "drawable";

    @Nullable
    private final TimeZone timeZone;
    private final int drawable;
    private final int ambientDrawable;


    SkylineScreen(@Nullable TimeZone timeZone, int drawable, int ambientDrawable) {
        this.timeZone = timeZone;
        this.drawable = drawable;
        this.ambientDrawable = ambientDrawable;
    }

    @NonNull
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        if (timeZone != null) {
            json.put(TIMEZONE, timeZone.getID());
        }
        json.put(DRAWABLE, drawable);
        return json;
    }

    @NonNull
    public static SkylineScreen fromJson(@NonNull JSONObject json) throws JSONException {

        final String timeZoneId = json.optString(TIMEZONE, null);
        TimeZone timeZone = timeZoneId == null ? null : TimeZone.getTimeZone(timeZoneId);
        int drawable = json.optInt(DRAWABLE, 0);

        return new SkylineScreen(timeZone, drawable, 0);
    }

    @Nullable
    public TimeZone getTimeZone() {
        return timeZone;
    }

    public int getDrawable() {
        return drawable;
    }

    public int getAmbientDrawable() {
        return ambientDrawable;
    }
}
