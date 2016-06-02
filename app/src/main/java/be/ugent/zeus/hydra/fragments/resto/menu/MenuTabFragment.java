package be.ugent.zeus.hydra.fragments.resto.menu;

import android.os.Bundle;
import be.ugent.zeus.hydra.activities.resto.MenuActivity;
import be.ugent.zeus.hydra.models.resto.RestoMenu;

/**
 * Displays the resto menu for one day.
 *
 * For now it is bound with the MenuActivity.
 */
public class MenuTabFragment extends MenuFragment {
    /**
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section number.
     *
     * This is for use in the main activity where tabs are used.
     */
    public static MenuTabFragment newInstance(int sectionNumber) {
        MenuTabFragment fragment = new MenuTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Get the menu to display. This function provides the data for the fragment.
     *
     * @return The data to display.
     */
    @Override
    public RestoMenu provideMenu() {
        MenuActivity activity = (MenuActivity) getActivity();
        return activity.getMenu(getArguments().getInt(ARG_SECTION_NUMBER));
    }
}