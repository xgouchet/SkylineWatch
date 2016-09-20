package fr.xgouchet.skylinewatch.ui;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import fr.xgouchet.skylinewatch.R;

/**
 * @author Xavier Gouchet
 */
public class ScreenItemView extends FrameLayout implements WearableListView.OnCenterProximityListener {

    final ImageView image;
    final TextView text;

    public ScreenItemView(Context context) {
        super(context);
        View.inflate(context, R.layout.screen_item, this);
        image = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);

    }


    @Override
    public void onCenterPosition(boolean b) {
        text.animate().scaleX(1f).scaleY(1f).alpha(1);
    }

    @Override
    public void onNonCenterPosition(boolean b) {
        text.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
    }
}
