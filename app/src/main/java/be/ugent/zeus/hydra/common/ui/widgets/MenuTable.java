package be.ugent.zeus.hydra.common.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.resto.RestoMenu;

/**
 * View to display the table. Use flags to decide what to show and what not.
 *
 * @author Niko Strijbol
 */
public class MenuTable extends TableLayout {

    /**
     * Flags to indicate what should be displayed by the menu.
     */
    @IntDef(
            flag = true,
            value = {DisplayKind.MAIN, DisplayKind.SOUP, DisplayKind.VEGETABLES, DisplayKind.ALL}
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface DisplayKind {
        int MAIN = 1; // 001
        int SOUP = 1 << 1; // 010
        int VEGETABLES = 1 << 2; // 100
        int ALL = 7; // 111
    }


    private DisplayableMenu menu;
    @DisplayKind
    private int displayedKinds;
    private boolean selectable;
    private boolean showTitles;

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

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MenuTable, 0, 0);

        try {
            //The menu defaults to the one with the hide functionality.
            displayedKinds = a.getInt(R.styleable.MenuTable_showKind, DisplayKind.ALL);
            selectable = a.getBoolean(R.styleable.MenuTable_selectable, false);
            showTitles = a.getBoolean(R.styleable.MenuTable_showTitles, false);
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

        final int rowPadding = getContext().getResources().getDimensionPixelSize(R.dimen.material_baseline_grid_1x);

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
                v.setTextAppearance(getContext(), R.style.Subhead);
            } else {
                v.setTextAppearance(R.style.Subhead);
            }
            v.setPadding(
                    v.getPaddingLeft(),
                    rowPadding,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );
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
     * @param menu The menu to display.
     */
    public void setMenu(RestoMenu menu) {
        this.menu = new DisplayableMenu(menu, selectable);
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

    private boolean mustShow(@DisplayKind int kind) {
        return (displayedKinds & kind) == kind;
    }

    /**
     * Add content.
     */
    private void populate() {

        setColumnStretchable(1, true);
        setColumnShrinkable(1, true);

        if (!menu.menu.isOpen()) {
            createTitle(getContext().getString(R.string.resto_menu_not_available), false);
            return;
        }

        if (mustShow(DisplayKind.MAIN)) {
            if (showTitles) {
                createTitle(getContext().getString(R.string.resto_menu_main_dish));
            }
            menu.addMainViews(this);
        }

        if (mustShow(DisplayKind.SOUP)) {
            if (showTitles) {
                createTitle(getContext().getString(R.string.resto_menu_soup));
            }
            menu.addSoupViews(this);
        }

        if (mustShow(DisplayKind.VEGETABLES)) {
            if (showTitles) {
                createTitle(getContext().getString(R.string.resto_menu_vegetables));
            }
            menu.addVegetableViews(this);
        }
    }
}