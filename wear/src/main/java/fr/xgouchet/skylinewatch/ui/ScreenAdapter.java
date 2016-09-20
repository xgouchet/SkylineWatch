package fr.xgouchet.skylinewatch.ui;


import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.ViewGroup;

import java.util.List;

import fr.xgouchet.skylinewatch.SkylineApplication;
import fr.xgouchet.skylinewatch.SkylineScreen;

/**
 * @author Xavier Gouchet
 */
public class ScreenAdapter extends WearableListView.Adapter {

    final List<SkylineScreen> screens;

    public ScreenAdapter(List<SkylineScreen> screens) {
        this.screens = screens;
    }

    public ScreenAdapter(Context context) {
        screens = ((SkylineApplication) context.getApplicationContext()).getScreens();
    }


    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScreenViewHolder(new ScreenItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        ScreenViewHolder screenViewHolder = (ScreenViewHolder) holder;
        screenViewHolder.bindScreen(screens.get(position));
    }

    @Override
    public int getItemCount() {
        return screens.size();
    }
}
