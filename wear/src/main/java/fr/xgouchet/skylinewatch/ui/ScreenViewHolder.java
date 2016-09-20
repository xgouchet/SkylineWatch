package fr.xgouchet.skylinewatch.ui;

import android.support.wearable.view.WearableListView;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.TimeZone;

import butterknife.BindView;
import fr.xgouchet.skylinewatch.R;
import fr.xgouchet.skylinewatch.SkylineScreen;
import fr.xgouchet.skylinewatch.SkylineService;

import static butterknife.ButterKnife.bind;

/**
 * @author Xavier Gouchet
 */
public class ScreenViewHolder extends WearableListView.ViewHolder {

    @BindView(R.id.text)
    TextView name;
    @BindView(R.id.image)
    ImageView image;

    public ScreenViewHolder(View itemView) {
        super(itemView);
        bind(this, itemView);
    }

    public void bindScreen(SkylineScreen skylineScreen) {
        final TimeZone timeZone = skylineScreen.getTimeZone();
        Time time = new Time();
        time.clear(timeZone == null ? TimeZone.getDefault().getID() : timeZone.getID());
        time.setToNow();

        itemView.setBackgroundResource(SkylineService.BACKGROUND_RSC[time.hour]);
        name.setText(timeZone == null ? "Local" : timeZone.getDisplayName());
        image.setImageResource(skylineScreen.getDrawable());
    }
}
