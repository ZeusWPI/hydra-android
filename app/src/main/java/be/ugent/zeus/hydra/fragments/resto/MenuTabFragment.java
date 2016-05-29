package be.ugent.zeus.hydra.fragments.resto;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
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

    private TextView createTitle(View view, String title) {
        TextView textView = new TextView(view.getContext());
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(p);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(title);
        return textView;
    }

    @Override
    protected void convert(View view) {
        LinearLayout l = (LinearLayout) view.findViewById(R.id.resto_menu_main);
        l.removeAllViews();

        l.addView(createTitle(view, "Hoofdgerechten"));
        l.addView(makeTableDishes(view, data.getMainDishes()));

        l.addView(createTitle(view, "Bijgerechten"));
        l.addView(makeTableDishes(view, data.getSideDishes()));

        l.addView(createTitle(view, "Groenten"));
        l.addView(makeVegetables(view, data.getVegetables()));
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