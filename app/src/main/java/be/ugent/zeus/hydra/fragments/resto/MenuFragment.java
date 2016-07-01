package be.ugent.zeus.hydra.fragments.resto;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.resto.RestoMeal;
import be.ugent.zeus.hydra.models.resto.RestoMenu;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Displays the resto menu for one day.
 *
 * The subclasses should decided in what manner the data is represented.
 */
public class MenuFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this fragment.
     */
    private static final String ARG_DATA_MENU = "resto_menu";
    private static final String ARG_PADDING = "padding";

    /**
     * The fragment argument representing the section number for this fragment.
     */
    private RestoMenu data;
    private boolean padding;

    public MenuFragment() {}

    public static MenuFragment newInstance(RestoMenu menu) {
        return newInstance(menu, false);
    }

    public static MenuFragment newInstance(RestoMenu menu, boolean padding) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DATA_MENU, menu);
        args.putBoolean(ARG_PADDING, padding);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Some implementations may wish to persist the data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = getArguments().getParcelable(ARG_DATA_MENU);
        padding = getArguments().getBoolean(ARG_PADDING);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        if(padding) {
            rootView = inflater.inflate(R.layout.fragment_menu_padding, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        }

        convert(rootView);
        return rootView;
    }

    /**
     * Make a tableview.
     */
    private void convert(View view) {
        LinearLayout l = $(view, R.id.resto_menu_main);
        l.removeAllViews();

        l.addView(createTitle(view, "Hoofdgerechten"));
        l.addView(makeTableDishes(view, data.getMainDishes()));

        l.addView(createTitle(view, "Bijgerechten"));
        l.addView(makeTableDishes(view, data.getSideDishes()));

        l.addView(createTitle(view, "Groenten"));
        l.addView(makeVegetables(view, data.getVegetables()));
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

    /**
     * Create a suitable table.
     */
    private static TableLayout createTable(View view) {
        TableLayout tableLayout = new TableLayout(view.getContext());
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tableLayout.setLayoutParams(p);
        tableLayout.setColumnStretchable(1, true);
        return tableLayout;
    }

    public static TableLayout makeTableDishes(View view, List<RestoMeal> meals) {
        TableLayout tableLayout = createTable(view);

        for (RestoMeal meal : meals) {

            TableRow tr = new TableRow(view.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tr.setPadding(0, 4, 0, 4);
            tr.setLayoutParams(lp);

            // set correct image according to kind
            ImageView imageView = new ImageView(view.getContext());
            switch (meal.getKind()) {
                case "soup":
                    imageView.setImageResource(R.drawable.soep);
                    break;
                case "meat":
                    imageView.setImageResource(R.drawable.vlees);
                    break;
                case "fish":
                    imageView.setImageResource(R.drawable.vis);
                    break;
                case "vegetarian":
                    imageView.setImageResource(R.drawable.vegi);
                    break;
                default:
                    imageView.setImageResource(R.drawable.soep);
            }

            imageView.setPadding(0, 5, 0, 0);

            TextView tvCenter = new TextView(view.getContext());
            tvCenter.setPadding(25, 0, 0, 0);
            tvCenter.setLayoutParams(lp);
            tvCenter.setText(meal.getName());
            TextView tvRight = new TextView(view.getContext());
            tvRight.setLayoutParams(lp);
            tvRight.setText(meal.getPrice());
            tvRight.setGravity(Gravity.END);


            tr.addView(imageView);
            tr.addView(tvCenter);
            tr.addView(tvRight);

            tableLayout.addView(tr);
        }

        return tableLayout;
    }

    public static TableLayout makeVegetables(View view, List<String> vegetables) {
        TableLayout tableLayout = createTable(view);

        for (String veg: vegetables) {

            TableRow tr = new TableRow(view.getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(lp);

            ImageView imageView = new ImageView(view.getContext());

            imageView.setImageResource(R.drawable.groenten);

            imageView.setPadding(0,6,0,0);

            TextView tvCenter = new TextView(view.getContext());
            tvCenter.setPadding(25,0,0,0);
            tvCenter.setLayoutParams(lp);
            tvCenter.setText(veg);

            tr.addView(imageView);
            tr.addView(tvCenter);

            tableLayout.addView(tr);
        }

        return tableLayout;
    }

    public void setData(RestoMenu data) {
        this.data = data;
    }

    public RestoMenu getData() {
        return data;
    }
}