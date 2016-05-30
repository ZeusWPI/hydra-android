package be.ugent.zeus.hydra.fragments.resto.menu;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.resto.RestoMenu;

/**
 * For single use, without tabs and such. It also displays less.
 */
public class MenuSingleFragment extends MenuFragment {
    /**
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_DATA_MENU = "resto_menu";

    /**
     * Standalone usage.
     * @param menu The menu that should be displayed.
     */
    public static MenuFragment newInstance(RestoMenu menu) {
        MenuSingleFragment fragment = new MenuSingleFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA_MENU, menu);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void convert(View view) {
        LinearLayout l = (LinearLayout) view.findViewById(R.id.resto_menu_main);
        l.removeAllViews();
        l.addView(makeTableDishes(view, data.getMainDishes()));
    }

    /**
     * Get the menu to display. This function provides the data for the fragment.
     *
     * @return The data to display.
     */
    @Override
    public RestoMenu provideMenu() {
        return getArguments().getParcelable(ARG_DATA_MENU);
    }
}
