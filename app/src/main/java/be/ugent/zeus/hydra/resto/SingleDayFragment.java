package be.ugent.zeus.hydra.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.ui.widgets.MenuTable;

import static be.ugent.zeus.hydra.utils.FragmentUtils.requireArguments;

/**
 * Displays the resto menu for one day. This is mostly used in the view pager.
 *
 * @author Niko Strijbol
 */
public class SingleDayFragment extends Fragment {

    private static final String ARG_DATA_MENU = "resto_menu";

    private RestoMenu data;

    public static SingleDayFragment newInstance(RestoMenu menu) {
        SingleDayFragment fragment = new SingleDayFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA_MENU, menu);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = requireArguments(this).getParcelable(ARG_DATA_MENU);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        MenuTable table = view.findViewById(R.id.menu_table);
        table.setMenu(data);
        return view;
    }

    public void setData(RestoMenu data) {
        this.data = data;
    }

    public RestoMenu getData() {
        return data;
    }

    @Override
    public void onResume() {
        super.onResume();
        Reporting.getTracker(getContext())
                .log(new MenuEvent(getContext(), data));
    }

    private static class MenuEvent implements Event {

        private final String resto;
        private final RestoMenu menu;

        private MenuEvent(Context context, RestoMenu menu) {
            this.menu = menu;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            this.resto = RestoPreferenceFragment.getRestoEndpoint(context, sharedPreferences);
        }

        @Nullable
        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Reporting.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.itemCategory(), RestoMenu.class.getSimpleName());
            params.putString(names.itemId(), resto + menu.getDate().toString());
            params.putString("date", menu.getDate().toString());
            params.putString("resto", resto);
            return params;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().viewItem();
        }
    }
}