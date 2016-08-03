package be.ugent.zeus.hydra.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.resto.RestoMeal;
import be.ugent.zeus.hydra.models.resto.RestoMenu;

import java.util.List;

/**
 * View to display the table. Use flags to decide what to show and what not.
 *
 * @author Niko Strijbol
 */
public class MenuTable extends TableLayout {

    //Flags
    public static final int MAIN = 0;
    public static final int ALL = 1;

    private RestoMenu menu;
    private int mode = MAIN;

    public MenuTable(Context context) {
        super(context);
    }

    public MenuTable(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MenuTable, 0, 0);

        try {
            //The menu defaults to the one with the hide functionality.
            mode = a.getInt(R.styleable.MenuTable_show, 0);
        } finally {
            a.recycle();
        }
    }

    /**
     * Create and add a title view.
     *
     * @param title The title.
     */
    private void createTitle(String title, boolean span) {
        TableRow tr = new TableRow(getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tr.setPadding(0, 4, 0, 4);
        tr.setLayoutParams(lp);

        TextView v = new TextView(getContext());
        TableRow.LayoutParams textParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        if(span) {
            textParam.span = 3;
            v.setTextAppearance(getContext(), R.style.Material_Typography_Subhead);
        }
        v.setLayoutParams(textParam);
        v.setText(title);


        tr.addView(v);
        addView(tr);
    }

    /**
     * Create and add a title view.
     *
     * @param title The title.
     */
    private void createTitle(String title) {
        createTitle(title, true);
    }

    /**
     * Add dishes.
     *
     * @param meals The meals with dishes.
     */
    private void makeTableDishes(List<RestoMeal> meals) {
        for (RestoMeal meal : meals) {
            TableRow tr = new TableRow(getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tr.setPadding(0, 4, 0, 4);
            tr.setLayoutParams(lp);

            // set correct image according to kind
            ImageView imageView = new ImageView(getContext());
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

            TextView tvCenter = new TextView(getContext());
            tvCenter.setPadding(25, 0, 0, 0);
            tvCenter.setLayoutParams(lp);
            tvCenter.setText(meal.getName());
            TextView tvRight = new TextView(getContext());
            tvRight.setLayoutParams(lp);
            tvRight.setText(meal.getPrice());
            tvRight.setGravity(Gravity.END);


            tr.addView(imageView);
            tr.addView(tvCenter);
            tr.addView(tvRight);

            addView(tr);
        }
    }

    public void makeVegetables(List<String> vegetables) {
        for (String veg: vegetables) {

            TableRow tr = new TableRow(getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(lp);

            ImageView imageView = new ImageView(getContext());

            imageView.setImageResource(R.drawable.groenten);

            imageView.setPadding(0,6,0,0);

            TextView tvCenter = new TextView(getContext());
            tvCenter.setPadding(25,0,0,0);
            tvCenter.setLayoutParams(lp);
            tvCenter.setText(veg);

            tr.addView(imageView);
            tr.addView(tvCenter);

            addView(tr);
        }
    }

    /**
     * @param menu The menu to display.
     */
    public void setMenu(RestoMenu menu) {
        this.menu = menu;
        //Add data
        removeAllViewsInLayout();
        populate();
        invalidate();
        requestLayout();
    }

    /**
     * @return The menu being displayed.
     */
    public RestoMenu getMenu() {
        return menu;
    }

    /**
     * Add content.
     */
    private void populate() {

        if (!menu.isOpen()) {
            createTitle("De resto is gesloten op deze dag.", false);
            return;
        }

        setColumnStretchable(1, true);

        if (mode == ALL) {
            createTitle("Hoofdgerechten");
            makeTableDishes(menu.getMainDishes());
            createTitle("Bijgerechten");
            makeTableDishes(menu.getSideDishes());
            createTitle("Groenten");
            makeVegetables(menu.getVegetables());
        } else {
            makeTableDishes(menu.getMainDishes());
        }
    }
}
