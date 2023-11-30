/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import be.ugent.zeus.hydra.common.utils.StringUtils;
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
        normalStyle = ViewUtils.getAttr(context, R.attr.textAppearanceBodyMedium);
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
        if (meal.kind() == null) {
            return R.drawable.resto_meat;
        }
        id = switch (meal.kind()) {
            case "fish" -> R.drawable.resto_fish;
            case "vegan" -> R.drawable.resto_vegan;
            case "vegetarian" -> R.drawable.resto_vegetarian;
            case "soup" -> R.drawable.resto_soup;
            default -> R.drawable.resto_meat;
        };
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

        for (String vegetable : menu.vegetables()) {

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
        addMealViews(parent, menu.soups(), false);
    }

    /**
     * Add the views responsible for displaying the main dishes to the {@code parent} view.
     *
     * @param parent The view to which the child views will be added. This will be done by calling {@link
     *               ViewGroup#addView(View)}. This is also the view to get a context from.
     */
    void addMainViews(ViewGroup parent, boolean showAllergens) {
        addMealViews(parent, menu.mainDishes(), showAllergens);
    }

    /**
     * Add the views responsible for displaying the cold dishes to the {@code parent} view.
     *
     * @param parent The view to which the child views will be added. This will be done by calling {@link
     *               ViewGroup#addView(View)}. This is also the view to get a context from.
     */
    void addColdViews(ViewGroup parent, boolean showAllergens) {
        addMealViews(parent, menu.coldDishes(), showAllergens);
    }

    /**
     * @return True if this menu has main dishes.
     */
    boolean hasMainDishes() {
        return !menu.mainDishes().isEmpty();
    }

    boolean hasSoup() {
        return !menu.soups().isEmpty();
    }

    boolean hasMessage() {
        return menu.message() != null && !menu.message().isEmpty();
    }

    boolean hasVegetables() {
        return !menu.vegetables().isEmpty();
    }
    
    boolean hasColdDishes() {
        return !menu.coldDishes().isEmpty();
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
    private void addMealViews(ViewGroup parent, List<RestoMeal> meals, boolean showAllergens) {
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
            String name = meal.name();
            TextView tvCenter = makeCenterTextView(context, name, lp);
            TextView tvRight = new MaterialTextView(context, null, normalStyle);
            tvRight.setLayoutParams(lp);
            tvRight.setText(meal.price());
            tvRight.setGravity(Gravity.END);

            tr.addView(imageView);
            tr.addView(tvCenter);
            tr.addView(tvRight);

            parent.addView(tr);
            
            // Add another row with allergens if required.
            if (showAllergens && !meal.allergens().isEmpty()) {
                TableRow allergenRow = new TableRow(context);
                allergenRow.setPadding(0, rowPadding, 0, rowPadding);
                allergenRow.setLayoutParams(lp);
                
                // First column is for the icon and empty.
                allergenRow.addView(new View(context));
                String allergens = StringUtils.formatList(meal.allergens());
                TextView allergenView = makeCenterTextView(context, allergens, lp);
                allergenView.setEnabled(false);
                allergenRow.addView(allergenView);
                allergenRow.addView(new View(context));
                parent.addView(allergenRow);
            }
        }
    }
}
