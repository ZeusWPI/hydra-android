package be.ugent.zeus.hydra.ui.main.resto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.widgets.MenuTable;
import be.ugent.zeus.hydra.resto.RestoMenu;

/**
 * Displays the resto menu for one day. This is mostly used in the view pager.
 *
 * @author Niko Strijbol
 */
public class MenuFragment extends Fragment {

    private static final String ARG_DATA_MENU = "resto_menu";

    private RestoMenu data;

    public static MenuFragment newInstance(RestoMenu menu) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA_MENU, menu);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getArguments().getParcelable(ARG_DATA_MENU);
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
}