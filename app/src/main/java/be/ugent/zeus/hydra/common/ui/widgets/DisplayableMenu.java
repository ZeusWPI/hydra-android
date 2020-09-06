package be.ugent.zeus.hydra.common.ui.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;

import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.utils.ViewUtils;
import be.ugent.zeus.hydra.resto.RestoMeal;
import be.ugent.zeus.hydra.resto.RestoMenu;
import com.google.android.material.textview.MaterialTextView;

/**
 * Helper class to display meals.
 * <p>
 * This is an immutable class.
 *
 * @author Niko Strijbol
 */
public class DisplayableMenu {

    private static final int ROW_PADDING_DP = 2;

    /**
     * The menu for which this displayable menu makes stuff.
     */
    final RestoMenu menu;
    private final boolean selectable;
    private final int normalStyle;

    DisplayableMenu(Context context, RestoMenu menu, boolean selectable) {
        this.menu = menu;
        this.selectable = selectable;
        normalStyle = ViewUtils.getAttr(context, R.attr.textAppearanceBody2);
    }

    /**
     * Make an image view. This image view has attributes set that are useful in the context of displaying it in the
     * menu.
     *
     * @param id The ID of the drawable. Can be a vector.
     * @return The image view.
     */
    private static ImageView makeImageView(Context context, @DrawableRes int id) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setImageDrawable(AppCompatResources.getDrawable(context, id));
        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        return imageView;
    }

    @DrawableRes
    public static int getDrawable(RestoMeal meal) {
        //Set the correct image.
        @DrawableRes int id;
        if (meal.getKind() == null) {
            return R.drawable.resto_meat;
        }
        switch (meal.getKind()) {
            case "fish":
                id = R.drawable.resto_fish;
                break;
            case "vegan":
                id = R.drawable.resto_vegan;
                break;
            case "vegetarian":
                id = R.drawable.resto_vegetarian;
                break;
            case "soup":
                id = R.drawable.resto_soup;
                break;
            default:
            case "meat":
                id = R.drawable.resto_meat;
        }
        return id;
    }

    /**
     * Add the view responsible for displaying the vegetables to the {@code parent} view.
     *
     * @param parent The view to which the child views will be added. This will be done by calling {@link
     *               ViewGroup#addView(View)}. This is also the view to get a context from.
     */
    void addVegetableViews(ViewGroup parent) {

        final Context context = parent.getContext();
        final int rowPadding = ViewUtils.convertDpToPixelInt(ROW_PADDING_DP, context);

        for (String vegetable : menu.getVegetables()) {

            TableRow tr = new TableRow(context);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(lp);
            tr.setPadding(0, rowPadding, 0, rowPadding);

            ImageView imageView = makeImageView(context, R.drawable.resto_vegetables);

            TextView tvCenter = makeCenterTextView(context, vegetable, lp);

            tr.addView(imageView);
            tr.addView(tvCenter);

            parent.addView(tr);
        }
    }

    /**
     * Add the views responsible for displaying the soups to the {@code parent} view.
     *
     * @param parent The view to which the child views will be added. This will be done by calling {@link
     *               ViewGroup#addView(View)}. This is also the view to get a context from.
     */
    void addSoupViews(ViewGroup parent) {
        addMealViews(parent, menu.getSoups());
    }

    /**
     * Add the views responsible for displaying the main dishes to the {@code parent} view.
     *
     * @param parent The view to which the child views will be added. This will be done by calling {@link
     *               ViewGroup#addView(View)}. This is also the view to get a context from.
     */
    void addMainViews(ViewGroup parent) {
        addMealViews(parent, menu.getMainDishes());
    }

    /**
     * @return True if this menu has main dishes.
     */
    boolean hasMainDishes() {
        return !menu.getMainDishes().isEmpty();
    }

    boolean hasSoup() {
        return !menu.getSoups().isEmpty();
    }

    boolean hasMessage() {
        return menu.getMessage() != null && !menu.getMessage().isEmpty();
    }

    boolean hasVegetables() {
        return !menu.getVegetables().isEmpty();
    }

    /**
     * Make center text.
     *
     * @param text The text.
     * @param lp   The layout param.
     * @return The text view.
     */
    private TextView makeCenterTextView(Context context, String text, TableRow.LayoutParams lp) {
        TextView tvCenter = new MaterialTextView(context, null, normalStyle);
        tvCenter.setTextIsSelectable(selectable);
        tvCenter.setPadding(ViewUtils.convertDpToPixelInt(16, context), 0, 0, 0);
        tvCenter.setLayoutParams(lp);
        tvCenter.setText(text);
        return tvCenter;
    }

    /**
     * Add the dishes from the list to the parent view.
     *
     * @param parent The parent view.
     * @param meals  The meals with dishes.
     */
    private void addMealViews(ViewGroup parent, List<RestoMeal> meals) {

        final Context context = parent.getContext();
        final int rowPadding = ViewUtils.convertDpToPixelInt(ROW_PADDING_DP, context);

        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

        for (RestoMeal meal : meals) {
            TableRow tr = new TableRow(context);
            tr.setPadding(0, rowPadding, 0, rowPadding);
            tr.setLayoutParams(lp);

            //Set the correct image.
            @DrawableRes final int id = getDrawable(meal);

            ImageView imageView = makeImageView(context, id);
            TextView tvCenter = makeCenterTextView(context, meal.getName(), lp);
            TextView tvRight = new MaterialTextView(context, null, normalStyle);
            tvRight.setLayoutParams(lp);
            tvRight.setText(meal.getPrice());
            tvRight.setGravity(Gravity.END);

            tr.addView(imageView);
            tr.addView(tvCenter);
            tr.addView(tvRight);

            parent.addView(tr);
        }
    }
}
