package be.ugent.zeus.hydra.feed.cards.implementations;

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
     *
     * @implNote This method is normally called only after {@link be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder#populate(Object)}
     *         has been called (if implemented in the view holder). The implementation should be prepared to handle
     *         cases where they are not in the right state; Android is notorious for view related events happening at
     *         the wrong time.
     */
    void onCreateMenu(Menu menu);
}
