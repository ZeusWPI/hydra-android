package be.ugent.zeus.hydra.ui.common.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.resto.RestoMeal;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.ui.common.ViewUtils;

import java.util.List;

/**
 * View to display the table. Use flags to decide what to show and what not.
 *
 * @author Niko Strijbol
 */
public class MenuTable extends TableLayout {

    /**
     * Indicates only the main items will be shown. Cannot be used together with {@link #ALL}.
     */
    public static final int MAIN = 0;
    /**
     * Indicates all items will be shown. Cannot be used together with {@link #MAIN}.
     */
    public static final int ALL = 1;

    private RestoMenu menu;
    private int mode = MAIN;
    private boolean selectable;

    public MenuTable(Context context) {
        super(context);
    }

    public MenuTable(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MenuTable, 0, 0);

        try {
            //The menu defaults to the one with the hide functionality.
            mode = a.getInt(R.styleable.MenuTable_show, 0);
            selectable = a.getBoolean(R.styleable.MenuTable_selectable, false);
        } finally {
            a.recycle();
        }
    }

    /**
     * Create and insert a title view.
     *
     * @param title The title.
     */
    private void createTitle(String title, boolean span) {

        final int rowPadding = ViewUtils.convertDpToPixelInt(4, getContext());

        TableRow tr = new TableRow(getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tr.setPadding(0, rowPadding, 0, rowPadding);
        tr.setLayoutParams(lp);

        TextView v = new TextView(getContext());
        v.setTextIsSelectable(selectable);
        TableRow.LayoutParams textParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        textParam.span = 3;
        if (span) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //noinspection deprecation
                v.setTextAppearance(getContext(), R.style.Material_Typography_Subhead);
            } else {
                v.setTextAppearance(R.style.Material_Typography_Subhead);
            }
        }
        v.setLayoutParams(textParam);
        v.setText(title);

        tr.addView(v);
        addView(tr);
    }

    /**
     * Create and insert a title view.
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

        final int rowPadding = ViewUtils.convertDpToPixelInt(2, getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        for (RestoMeal meal : meals) {
            TableRow tr = new TableRow(getContext());
            tr.setPadding(0, rowPadding, 0, rowPadding);
            tr.setLayoutParams(lp);

            //Set the correct image.
            @DrawableRes
            final int id;
            switch (meal.getKind()) {
                case "meat":
                    id = R.drawable.resto_meat;
                    break;
                case "fish":
                    id = R.drawable.resto_fish;
                    break;
                case "vegetarian":
                    id = R.drawable.resto_vegi;
                    break;
                case "soup":
                default:
                    id = R.drawable.resto_soup;
            }

            ImageView imageView = makeImageView(id);
            TextView tvCenter = makeCenterTextView(meal.getName(), lp);
            TextView tvRight = new TextView(getContext());
            tvRight.setLayoutParams(lp);
            tvRight.setTextIsSelectable(selectable);
            tvRight.setText(meal.getPrice());
            tvRight.setGravity(Gravity.END);

            tr.addView(imageView);
            tr.addView(tvCenter);
            tr.addView(tvRight);

            addView(tr);
        }
    }

    public void makeVegetables(List<String> vegetables) {

        final int rowPadding = ViewUtils.convertDpToPixelInt(2, getContext());

        for (String veg: vegetables) {

            TableRow tr = new TableRow(getContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(lp);
            tr.setPadding(0, rowPadding, 0, rowPadding);

            ImageView imageView = makeImageView(R.drawable.resto_vegetables);

            TextView tvCenter = makeCenterTextView(veg, lp);

            tr.addView(imageView);
            tr.addView(tvCenter);

            addView(tr);
        }
    }

    /**
     * Make an image view.
     *
     * @param id The ID of the drawable. Can be a vector.
     * @return The imageview.
     */
    private ImageView makeImageView(@DrawableRes int id) {
        ImageView imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(AppCompatResources.getDrawable(getContext(), id));
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        return imageView;
    }

    /**
     * Make center text.
     * @param text The text.
     * @param lp The layout param.
     * @return The text view.
     */
    private TextView makeCenterTextView(String text, TableRow.LayoutParams lp) {
        TextView tvCenter = new TextView(getContext());
        tvCenter.setTextIsSelectable(selectable);
        tvCenter.setPadding(ViewUtils.convertDpToPixelInt(16, getContext()),0,0,0);
        tvCenter.setLayoutParams(lp);
        tvCenter.setText(text);
        return tvCenter;
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
     * Add content.
     */
    @SuppressLint("SetTextI18n")
    private void populate() {

        setColumnStretchable(1, true);
        setColumnShrinkable(1, true);

        if (!menu.isOpen()) {
            createTitle(getContext().getString(R.string.resto_closed), false);
            return;
        }

        if (mode == ALL) {
            //Add actual menu data
            createTitle(getContext().getString(R.string.resto_main_dish));
            makeTableDishes(menu.getMainDishes());
            createTitle(getContext().getString(R.string.resto_side_dish));
            makeTableDishes(menu.getSideDishes());
            createTitle(getContext().getString(R.string.vegetables));
            makeVegetables(menu.getVegetables());
            //Add date data
            createTitle(String.format(getContext().getString(R.string.resto_menu_date), menu.getDate().toString()), false);
        } else {
            makeTableDishes(menu.getMainDishes());
        }
    }
}