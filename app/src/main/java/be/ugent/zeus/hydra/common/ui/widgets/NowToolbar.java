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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.*;
import androidx.annotation.DrawableRes;
import androidx.annotation.MenuRes;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.MenuHandler;

/**
 * A custom view to encapsulate the Google Now-like bar above the cards on the home tab.
 * <p>
 * While this view is fairly generic, the default values are set to be useful for us, such as the tint and menu content.
 * If you want to use this elsewhere, you'll need to adjust some things.
 * <p>
 * At present, only one constructor is correctly implemented, if you use it with others, you'll need to implement
 * the correct constructor.
 *
 * @author Niko Strijbol
 */
public class NowToolbar extends LinearLayout {

    private TextView titleView;
    private ImageView menuButton;
    private ImageView iconView;

    @MenuRes
    private int menu;

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
        // Inflate from XML.
        inflate(getContext(), R.layout.x_now_toolbar, this);

        iconView = findViewById(R.id.now_toolbar_icon);
        titleView = findViewById(R.id.now_toolbar_title);
        menuButton = findViewById(R.id.now_toolbar_menu);

        // If no attributes, stop here.
        if (set == null) {
            return;
        }

        TypedArray a = getContext().getTheme().obtainStyledAttributes(set, R.styleable.NowToolbar, 0, 0);

        try {
            this.menu = a.getResourceId(R.styleable.NowToolbar_menu, R.menu.now_toolbar_default);
            titleView.setText(a.getString(R.styleable.NowToolbar_title));
            if (!a.getBoolean(R.styleable.NowToolbar_showMenu, true)) {
                menuButton.setVisibility(GONE);
            }
            if (a.hasValue(R.styleable.NowToolbar_icon)) {
                setIcon(a.getResourceId(R.styleable.NowToolbar_icon, R.drawable.tabs_home));
            }
        } finally {
            a.recycle();
        }
    }

    /**
     * Set the listener for the overflow button in the right corner of the toolbar.
     *
     * @param handler The listener.
     */
    public void setOnMenuClickListener(MenuHandler handler) {
        menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.inflate(menu);
            handler.onCreateMenu(popup.getMenu());
            popup.setOnMenuItemClickListener(handler);
            popup.show();
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
}