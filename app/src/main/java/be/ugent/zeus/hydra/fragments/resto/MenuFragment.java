package be.ugent.zeus.hydra.fragments.resto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.resto.RestoMeal;
import be.ugent.zeus.hydra.models.resto.RestoMenu;

import java.util.List;

/**
 * Displays the resto menu for one day.
 *
 * The subclasses should decided in what manner the data is represented.
 */
public abstract class MenuFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this fragment.
     */
    protected RestoMenu data;

    public MenuFragment() {}

    /**
     * Some implementations may wish to persist the data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = provideMenu();
    }

    /**
     * Get the menu to display. This function provides the data for the fragment.
     *
     * @return The data to display.
     */
    public abstract RestoMenu provideMenu();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        convert(rootView);
        return rootView;
    }

    /**
     * Make a tableview.
     */
    protected abstract void convert(View view);

    /**
     * Create a suitable table.
     */
    private TableLayout createTable(View view) {
        TableLayout tableLayout = new TableLayout(view.getContext());
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tableLayout.setLayoutParams(p);
        tableLayout.setColumnStretchable(1, true);
        return tableLayout;
    }

    protected TableLayout makeTableDishes(View view, List<RestoMeal> meals) {
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

    protected TableLayout makeVegetables(View view, List<String> vegetables) {
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
