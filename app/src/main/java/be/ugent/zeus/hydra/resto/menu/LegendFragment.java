package be.ugent.zeus.hydra.resto.menu;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.analytics.Analytics;
import be.ugent.zeus.hydra.common.analytics.BaseEvents;
import be.ugent.zeus.hydra.common.analytics.Event;
import be.ugent.zeus.hydra.resto.RestoMenu;

/**
 * Display a legend for the resto menu.
 *
 * @author Niko Strijbol
 */
public class LegendFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resto_menu_legend, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Analytics.getTracker(getContext())
                .log(new ViewEvent());
    }

    private static class ViewEvent implements Event {

        @Nullable
        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Analytics.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.itemCategory(), RestoMenu.class.getSimpleName());
            params.putString(names.itemId(), "LEGEND");
            return params;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Analytics.getEvents().viewItem();
        }
    }
}