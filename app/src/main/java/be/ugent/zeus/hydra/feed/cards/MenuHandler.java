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

package be.ugent.zeus.hydra.feed.cards;

import android.view.Menu;
import android.widget.PopupMenu;

/**
 * Allow individual cards to alter the menu.
 *
 * @author Niko Strijbol
 */
public interface MenuHandler extends PopupMenu.OnMenuItemClickListener {

    /**
     * Called when the menu is created. This allows implementations to change the menu; this method is called after the
     * menu has been inflated.
     *
     * @param menu The menu.
     * @implNote This method is normally called only after {@link be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder#populate(Object)}
     * has been called (if implemented in the view holder). The implementation should be prepared to handle
     * cases where they are not in the right state; Android is notorious for view related events happening at
     * the wrong time.
     */
    void onCreateMenu(Menu menu);
}
