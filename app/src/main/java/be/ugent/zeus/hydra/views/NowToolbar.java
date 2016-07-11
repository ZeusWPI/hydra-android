package be.ugent.zeus.hydra.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;

/**
 * A custom view to encapsulate the Google Now-like bar above the cards on the home tab.
 *
 * While this view is fairly generic, the default values are set to be useful for us, such as the tint and menu content.
 * If you want to use this elsewhere, you'll need to adjust some things.
 *
 * At present, only one constructor is correctly implemented, if you use it with others, you'll need to implement
 * the correct constructor.
 *
 * @author Niko Strijbol
 */
public class NowToolbar extends LinearLayout {

    private TextView titleView;
    private ImageView menuButton;

    //Menu-related things.
    @MenuRes private int menu;

    public NowToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NowToolbar, 0, 0);

        try {
            //The menu defaults to the one with the hide functionality.
            int menu = a.getInt(R.styleable.NowToolbar_menu, R.menu.now_toolbar_hide);
            Drawable icon = a.getDrawable(R.styleable.NowToolbar_icon);
            String title = a.getString(R.styleable.NowToolbar_title);
            initialize(menu, icon, title);
        } finally {
            a.recycle();
        }
    }

    /**
     * Initialize the view. A helper method that makes it easier to use different constructors.
     *
     * @param menu The resource ID of the menu to display.
     * @param icon The icon drawable.
     * @param title The title to display.
     */
    private void initialize(@MenuRes final int menu, Drawable icon, String title) {
        inflate(getContext(), R.layout.x_now_toolbar, this);
        ImageView iconView = $(R.id.now_toolbar_icon);
        titleView = $(R.id.now_toolbar_title);
        menuButton = $(R.id.now_toolbar_menu);
        this.menu = menu;

        iconView.setImageDrawable(icon);
        titleView.setText(title);
    }

    /**
     * Set the listener for the overflow button in the right corner of the toolbar.
     *
     * @param listener The listener.
     */
    public void setOnClickListener(final PopupMenu.OnMenuItemClickListener listener) {
        menuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(v.getContext(), v);
                popup.inflate(menu);
                popup.setOnMenuItemClickListener(listener);
                popup.show();
            }
        });
    }

    /**
     * @return The title that is currently displayed.
     */
    public CharSequence getTitle() {
        return titleView.getText();
    }

    /**
     * Set the title.
     *
     * @param title The title.
     */
    public void setTitle(CharSequence title) {
        titleView.setText(title);
        invalidate();
        requestLayout();
    }

    @NonNull
    public <T extends View> T $(@IdRes int id) {
        @SuppressWarnings("unchecked")
        T v = (T) findViewById(id);
        assert v != null;
        return v;
    }
}