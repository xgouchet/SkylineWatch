package fr.xgouchet.skylinewatch.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import fr.xgouchet.skylinewatch.R;

import static butterknife.ButterKnife.bind;

/**
 * @author Xavier Gouchet
 */
public class ScreenFragment extends Fragment {

    @BindView(R.id.screens_list)
    WearableListView screensList;

    ScreenAdapter screenAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.screens_config_fragment, container, false);

        bind(this, root);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        screenAdapter = new ScreenAdapter(getActivity());
        screensList.setAdapter(screenAdapter);
    }
}
