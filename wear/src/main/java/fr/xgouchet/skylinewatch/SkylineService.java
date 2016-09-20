package fr.xgouchet.skylinewatch;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author Xavier Gouchet
 */
public class SkylineService extends CanvasWatchFaceService {


    /*package*/ static final Typeface NORMAL_TYPEFACE = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
    /*package*/ static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    public static final int[] BACKGROUND_RSC = new int[]{
            R.drawable.background_00,
            R.drawable.background_01,
            R.drawable.background_02,
            R.drawable.background_03,
            R.drawable.background_04,
            R.drawable.background_05,
            R.drawable.background_06,
            R.drawable.background_07,
            R.drawable.background_08,
            R.drawable.background_09,
            R.drawable.background_10,
            R.drawable.background_11,
            R.drawable.background_12,
            R.drawable.background_13,
            R.drawable.background_14,
            R.drawable.background_15,
            R.drawable.background_16,
            R.drawable.background_17,
            R.drawable.background_18,
            R.drawable.background_19,
            R.drawable.background_20,
            R.drawable.background_21,
            R.drawable.background_22,
            R.drawable.background_23
    };
    public static final String AM_SUFFIX = " am";
    public static final String PM_SUFFIX = " pm";

    @Override
    public SkylineEngine onCreateEngine() {
        return new SkylineEngine(this);
    }

    static class EngineHandler extends Handler {

        public static final int MSG_UPDATE_TIME = 0;

        private final WeakReference<SkylineService.SkylineEngine> mWeakReference;

        public EngineHandler(SkylineService.SkylineEngine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            SkylineService.SkylineEngine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    class SkylineEngine extends CanvasWatchFaceService.Engine {

        /* Preferences */
        private boolean displayAmPm;
        private boolean displaySeconds;
        private boolean displayDate;

        private SkylineScreen[] screens = new SkylineScreen[]{
                new SkylineScreen(null, R.drawable.paris_skyline, R.drawable.paris_skyline_ambient),
                new SkylineScreen(TimeZone.getTimeZone("US/Central"), R.drawable.sl_miami, 0),
                new SkylineScreen(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"), 0, 0),
                new SkylineScreen(TimeZone.getTimeZone("America/Los_Angeles"), 0, 0),
                new SkylineScreen(TimeZone.getTimeZone("US/Alaska"), 0, 0),
                new SkylineScreen(TimeZone.getTimeZone("Asia/Kamchatka"), 0, 0),
                new SkylineScreen(TimeZone.getTimeZone("Etc/GMT-14"), 0, 0),
        };

        /* Internal states */
        Time time = new Time();

        private SkylineService skylineService;
        private final Handler engineHandler = new EngineHandler(this);
        private Paint timeTextPaint;
        private Paint dateTextPaint;
        private boolean isAmbient;

        private int screenIndex = 0;

        private float timeXOffset, timeYOffset, dateXOffset, dateYOffset;

        private boolean mLowBitAmbient;

        public SkylineEngine(SkylineService skylineService) {
            this.skylineService = skylineService;
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(skylineService)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());

            Resources resources = skylineService.getResources();
            timeYOffset = resources.getDimension(R.dimen.time_y_offset);
            dateYOffset = resources.getDimension(R.dimen.date_y_offset);


            createTextPaints(skylineService);
        }

        @Override
        public void onDestroy() {
            engineHandler.removeMessages(EngineHandler.MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            displaySeconds = SkylinePreferences.shouldDisplaySeconds(skylineService);
            displayDate = SkylinePreferences.shouldDisplayDate(skylineService);
            displayAmPm = SkylinePreferences.shouldDisplayAmPm(skylineService);

            updateTimer();
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = skylineService.getResources();
            boolean isRound = insets.isRound();
            timeXOffset = resources.getDimension(isRound ? R.dimen.time_x_offset_round : R.dimen.time_x_offset);
            dateXOffset = resources.getDimension(isRound ? R.dimen.date_x_offset_round : R.dimen.date_x_offset);

            timeTextPaint.setTextSize(resources.getDimension(isRound ? R.dimen.time_text_size_round : R.dimen.time_text_size));
            dateTextPaint.setTextSize(resources.getDimension(isRound ? R.dimen.date_text_size_round : R.dimen.date_text_size));
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (isAmbient != inAmbientMode) {
                isAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    timeTextPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            updateTimer();
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case WatchFaceService.TAP_TYPE_TOUCH:
                    break;
                case WatchFaceService.TAP_TYPE_TOUCH_CANCEL:
                    break;
                case WatchFaceService.TAP_TYPE_TAP:
                    screenIndex = (screenIndex + 1) % screens.length;
                    break;
            }
            invalidate();
        }

        @Override
        public void onDraw(@NonNull Canvas canvas, @NonNull Rect bounds) {


            TimeZone timeZone = screens[screenIndex].getTimeZone();
            if (timeZone == null) timeZone = TimeZone.getDefault();
            time.clear(timeZone.getID());
            time.setToNow();

            drawBackground(canvas, bounds);
            drawSkyline(canvas, bounds);
            drawTimeAndDate(canvas);
        }

        private void drawBackground(@NonNull Canvas canvas, @NonNull Rect bounds) {
            canvas.drawColor(Color.BLACK);
            if (!isInAmbientMode()) {
                Drawable background = skylineService.getDrawable(BACKGROUND_RSC[time.hour]);
                if (background == null) return;

                int margins = bounds.height() / 4;
                background.setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom - margins);
                background.draw(canvas);
            }
        }

        private void drawSkyline(@NonNull Canvas canvas, @NonNull Rect bounds) {
            int resource;
            Drawable skyline;
            if (isInAmbientMode()) {
                resource = screens[screenIndex].getAmbientDrawable();
            } else {
                resource = screens[screenIndex].getDrawable();
            }
            if (resource <= 0) return;
            skyline = skylineService.getDrawable(resource);
            if (skyline == null) return;

            int margins = bounds.height() / 4;
            skyline.setBounds(bounds.left, bounds.top + margins, bounds.right, bounds.bottom - margins);
            skyline.draw(canvas);
        }

        private void drawTimeAndDate(Canvas canvas) {
            String timeText;
            String formatSuffix;
            int hours;
            if (displayAmPm) {
                switch (time.hour) {
                    case 0:
                        hours = 12;
                        formatSuffix = AM_SUFFIX;
                        break;
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                        hours = time.hour;
                        formatSuffix = AM_SUFFIX;
                        break;
                    case 12:
                        hours = 12;
                        formatSuffix = PM_SUFFIX;
                        break;
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 20:
                    case 21:
                    case 22:
                    case 23:
                        hours = time.hour - 12;
                        formatSuffix = PM_SUFFIX;
                        break;
                    default:
                        hours = 0;
                        formatSuffix = " ?";
                }
            } else {
                hours = time.hour;
                formatSuffix = "";
            }

            if (displaySeconds && !isAmbient) {
                timeText = String.format(Locale.US, "%02d:%02d:%02d" + formatSuffix, hours, time.minute, time.second);
            } else {
                timeText = String.format(Locale.US, "%02d:%02d" + formatSuffix, hours, time.minute);
            }
            canvas.drawText(timeText, timeXOffset, timeYOffset, timeTextPaint);

            if (displayDate) {
                canvas.drawText(String.format(Locale.US, "%02d/%02d", time.monthDay, time.month), dateXOffset, dateYOffset, dateTextPaint);
            }
        }

        private void createTextPaints(@NonNull Context context) {
            int color = ContextCompat.getColor(context, R.color.digital_text);

            timeTextPaint = new Paint();
            timeTextPaint.setColor(color);
            timeTextPaint.setTypeface(NORMAL_TYPEFACE);
            timeTextPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.time_text_size));
            timeTextPaint.setAntiAlias(true);

            dateTextPaint = new Paint();
            dateTextPaint.setColor(color);
            dateTextPaint.setTypeface(NORMAL_TYPEFACE);
            dateTextPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.date_text_size));
            dateTextPaint.setAntiAlias(true);
        }

        private void updateTimer() {
            engineHandler.removeMessages(EngineHandler.MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                engineHandler.sendEmptyMessage(EngineHandler.MSG_UPDATE_TIME);
            }
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode() && displaySeconds;
        }

        public void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                engineHandler.sendEmptyMessageDelayed(EngineHandler.MSG_UPDATE_TIME, delayMs);
            }
        }
    }

}
