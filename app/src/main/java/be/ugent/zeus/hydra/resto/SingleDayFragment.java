package be.ugent.zeus.hydra.resto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.databinding.FragmentMenuBinding;

/**
 * Displays the resto menu for one day. This is mostly used in the view pager.
 *
 * @author Niko Strijbol
 */
public class SingleDayFragment extends Fragment {

    private static final String ARG_DATA_MENU = "resto_menu";
    private static final String URL = "https://studentenrestaurants.ugent.be/";

    private RestoMenu data;
    private FragmentMenuBinding binding;

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
        data = requireArguments().getParcelable(ARG_DATA_MENU);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.menuTable.setMenu(data);
        binding.orderButton.setOnClickListener(v -> {
            String url = URL + v.getContext().getString(R.string.value_info_endpoint);
            NetworkUtils.maybeLaunchBrowser(v.getContext(), url);
        });
    }

    public RestoMenu getData() {
        return data;
    }

    public void setData(RestoMenu data) {
        this.data = data;
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
