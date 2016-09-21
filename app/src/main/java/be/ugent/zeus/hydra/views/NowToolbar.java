package be.ugent.zeus.hydra.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
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
    private ImageView iconView;

    //Menu-related things.
    @MenuRes private int menu;

    public NowToolbar(Context context) {
        super(context);
        initialize(null);
    }

    public NowToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(attrs);
    }

    /**
     * Initialize the view. A helper method that makes it easier to use different constructors.
     */
    private void initialize(AttributeSet set) {
        //Inflate from XML
        inflate(getContext(), R.layout.x_now_toolbar, this);

        iconView = $(R.id.now_toolbar_icon);
        titleView = $(R.id.now_toolbar_title);
        menuButton = $(R.id.now_toolbar_menu);

        //If no attributes, stop here.
        if(set == null) {
            return;
        }

        //Get attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(set, R.styleable.NowToolbar, 0, 0);

        try {
            this.menu = a.getInt(R.styleable.NowToolbar_menu, R.menu.now_toolbar_hide);
            titleView.setText(a.getString(R.styleable.NowToolbar_title));
            if(!a.getBoolean(R.styleable.NowToolbar_showMenu, true)) {
                menuButton.setVisibility(GONE);
            }
            if(a.hasValue(R.styleable.NowToolbar_icon)) {
                setIcon(a.getResourceId(R.styleable.NowToolbar_icon, R.drawable.ic_tabs_home));
            }
        } finally {
            a.recycle();
        }
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
    }

    public void setIcon(@DrawableRes int id) {
        setIconTo(id);
        invalidate();
    }

    private void setIconTo(@DrawableRes int id) {
        iconView.setImageResource(id);
    }

    public void setMenu(@MenuRes int menu) {
        this.menu = menu;
    }

    @NonNull
    public <T extends View> T $(@IdRes int id) {
        @SuppressWarnings("unchecked")
        T v = (T) findViewById(id);
        assert v != null;
        return v;
    }
}