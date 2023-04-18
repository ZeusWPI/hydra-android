/*
 * Copyright (c) 2021 The Hydra authors
 * Copyright (c) 2022 Niko Strijbol
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
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.html.Utils;
import be.ugent.zeus.hydra.common.utils.ViewUtils;
import be.ugent.zeus.hydra.resto.RestoMenu;
import com.google.android.material.textview.MaterialTextView;

import static be.ugent.zeus.hydra.common.utils.PreferencesUtils.isSetIn;

/**
 * View to display the table. Use flags to decide what to show and what not.
 *
 * @author Niko Strijbol
 */
public class MenuTable extends TableLayout {

    private DisplayableMenu menu;
    @DisplayKind
    private int displayedKinds;
    private boolean selectable;
    private boolean showTitles;
    private boolean messagePaddingTop;
    private int normalStyle;
    
    private boolean showAllergens;

    public MenuTable(Context context) {
        super(context);
        init(context, null);
    }

    public MenuTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * Initiate the values from the attribute set.
     *
     * @param context The context.
     * @param attrs   The attribute set or null if not available.
     */
    private void init(Context context, @Nullable AttributeSet attrs) {

        // The warning is bogus, since it was only added in API 31.
        @SuppressWarnings("resource")
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MenuTable, 0, 0);

        try {
            displayedKinds = a.getInt(R.styleable.MenuTable_showKind, DisplayKind.ALL);
            selectable = a.getBoolean(R.styleable.MenuTable_selectable, false);
            showTitles = a.getBoolean(R.styleable.MenuTable_showTitles, false);
            messagePaddingTop = a.getBoolean(R.styleable.MenuTable_messagePaddingTop, false);
            normalStyle = ViewUtils.getAttr(context, R.attr.textAppearanceBodyMedium);
        } finally {
            a.recycle();
        }
    }

    /**
     * Create and insert a text view.
     *
     * @param text    The text to add.
     * @param isTitle If the text is a title and should be styled as such.
     * @param isHtml  If the text contains basic html.
     */
    private void createText(String text, boolean isTitle, boolean isHtml) {

        final int rowPadding = getContext().getResources().getDimensionPixelSize(R.dimen.material_baseline_grid_1x);

        TableRow tr = new TableRow(getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        tr.setPadding(0, 0, 0, rowPadding);
        tr.setLayoutParams(lp);

        TextView v;
        if (isTitle) {
            v = new MaterialTextView(getContext(), null);
        } else {
            v = new MaterialTextView(getContext(), null, normalStyle);
        }
        v.setTextIsSelectable(selectable);
        TableRow.LayoutParams textParam = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        textParam.span = 3;
        final int textPaddingTop;
        if (isTitle) {
            if (Build.VERSION.SDK_INT < 23) {
                v.setTextAppearance(getContext(), R.style.Hydra_Text_Subhead);
            } else {
                v.setTextAppearance(R.style.Hydra_Text_Subhead);
            }
            textPaddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.vertical_padding);
        } else if (messagePaddingTop) {
            textPaddingTop = getContext().getResources().getDimensionPixelSize(R.dimen.vertical_padding);
        } else {
            textPaddingTop = v.getPaddingTop();
        }
        // If the text is a title, set the title padding.
        // Else if we set message padding, set the message padding.
        // Otherwise keep the existing padding.
        v.setPadding(
                v.getPaddingLeft(),
                textPaddingTop,
                v.getPaddingRight(),
                v.getPaddingBottom()
        );
        v.setLayoutParams(textParam);
        if (isHtml) {
            v.setText(Utils.fromHtml(text));
        } else {
            v.setText(text);
        }

        tr.addView(v);
        addView(tr);
    }

    /**
     * Create and insert a title view.
     *
     * @param title The title.
     */
    private void createTitle(String title) {
        createText(title, true, false);
    }

    /**
     * @param menu The menu to display.
     */
    public void setMenu(RestoMenu menu, @DisplayKind int displayedKinds, boolean showAllergens) {
        this.menu = new DisplayableMenu(getContext(), menu, selectable);
        this.displayedKinds = displayedKinds;
        this.showAllergens = showAllergens;
        //Add data
        removeAllViewsInLayout();
        populate();
        invalidate();
        requestLayout();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public RestoMenu getMenu() {
        return menu.menu;
    }

    /**
     * @param menu The menu to display.
     */
    public void setMenu(RestoMenu menu, boolean showAllergens) {
        setMenu(menu, this.displayedKinds, showAllergens);
    }

    /**
     * Add content.
     */
    private void populate() {
        setColumnStretchable(1, true);
        setColumnShrinkable(1, true);

        // If there is no message and it is closed.
        if (menu.hasMessage()) {
            createText(menu.menu.getMessage(), false, true);
            if (menu.menu.isClosed()) {
                return;
            }
        }

        // Does not have a message at this point.
        // assert !menu.hasMessage();
        if (menu.menu.isClosed()) {
            createText(getContext().getString(R.string.resto_menu_not_available), false, true);
            return;
        }

        if (isSetIn(displayedKinds, DisplayKind.HOT) && menu.hasMainDishes()) {
            if (showTitles) {
                createTitle(getContext().getString(R.string.resto_menu_main_dish));
            }
            menu.addMainViews(this, showAllergens);
        }

        if (isSetIn(displayedKinds, DisplayKind.COLD) && menu.hasColdDishes()) {
            if (showTitles) {
                createTitle(getContext().getString(R.string.resto_menu_cold_dish));
            }
            menu.addColdViews(this, showAllergens);
        }

        if (isSetIn(displayedKinds, DisplayKind.SOUP) && menu.hasSoup()) {
            if (showTitles) {
                createTitle(getContext().getString(R.string.resto_menu_soup));
            }
            menu.addSoupViews(this);
        }

        if (isSetIn(displayedKinds, DisplayKind.VEGETABLES) && menu.hasVegetables()) {
            if (showTitles) {
                createTitle(getContext().getString(R.string.resto_menu_vegetables));
            }
            menu.addVegetableViews(this);
        }
    }

    /**
     * Flags to indicate what should be displayed by the menu.
     */
    @IntDef(
            flag = true,
            value = {DisplayKind.HOT, DisplayKind.COLD, DisplayKind.SOUP, DisplayKind.VEGETABLES, DisplayKind.ALL}
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayKind {
        int HOT = 1;
        int COLD = 1 << 1;
        int SOUP = 1 << 2;
        int VEGETABLES = 1 << 3;
        int ALL = HOT | COLD | SOUP | VEGETABLES;
    }
}
