package be.ugent.zeus.hydra.feed.cards.implementations.resto;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.commands.FeedCommand;
import be.ugent.zeus.hydra.feed.preferences.HomeFragment;

import static be.ugent.zeus.hydra.feed.cards.implementations.resto.RestoCardViewHolder.KindMenu.*;

/**
 * Command that saves and reverses changes to what is displayed in the menu.
 *
 * @author Niko Strijbol
 */
class RestoKindCommand implements FeedCommand {

    /**
     * Map the menu items to their resulting view state, i.e. what should be shown after the menu item is pressed.
     *
     * For example, the {@link RestoCardViewHolder.KindMenu#HIDE_SOUP}
     * menu item id is mapped to {@link HomeFragment.FeedRestoKind#MAIN}, meaning when the hide soup menu is pressed,
     * the resulting menu type is one that only shows the main menu.
     *
     * It is important to note that one of the two must always be shown, e.g. soup and/or main dishes, but not none.
     */
    private static final SparseArray<String> MENU_TO_FORWARD = new SparseArray<>();
    /**
     * The reverse mapping of {@link #MENU_TO_FORWARD}. This maps the menu items to their view state when the menu item
     * was pressed, effectively reversing the action.
     */
    private static final SparseArray<String> MENU_TO_REVERSE = new SparseArray<>();
    /**
     * Maps menu items to their message of completion, i.e. the message shown when the action has been performed.
     */
    private static final SparseIntArray MENU_TO_ITEM = new SparseIntArray();
    static {
        // When hiding soup, only main dishes remain.
        MENU_TO_FORWARD.append(HIDE_SOUP, HomeFragment.FeedRestoKind.MAIN);
        // When showing soup, all is shown.
        MENU_TO_FORWARD.append(SHOW_SOUP, HomeFragment.FeedRestoKind.ALL);
        // When hiding main dishes, only soup remains.
        MENU_TO_FORWARD.append(HIDE_MAIN, HomeFragment.FeedRestoKind.SOUP);
        // When showing main dishes, all is shown.
        MENU_TO_FORWARD.append(SHOW_MAIN, HomeFragment.FeedRestoKind.ALL);

        MENU_TO_REVERSE.append(HIDE_SOUP, MENU_TO_FORWARD.get(SHOW_SOUP));
        MENU_TO_REVERSE.append(SHOW_SOUP, MENU_TO_FORWARD.get(HIDE_SOUP));
        MENU_TO_REVERSE.append(HIDE_MAIN, MENU_TO_FORWARD.get(SHOW_MAIN));
        MENU_TO_REVERSE.append(SHOW_MAIN, MENU_TO_FORWARD.get(HIDE_MAIN));

        MENU_TO_ITEM.append(HIDE_SOUP, R.string.feed_card_hidden_menu_soup);
        MENU_TO_ITEM.append(SHOW_SOUP, R.string.feed_card_shown_menu_soup);
        MENU_TO_ITEM.append(HIDE_MAIN, R.string.feed_card_hidden_menu_main);
        MENU_TO_ITEM.append(SHOW_MAIN, R.string.feed_card_shown_menu_main);
    }

    @RestoCardViewHolder.KindMenu
    private final int selectedOption;

    RestoKindCommand(@RestoCardViewHolder.KindMenu int selectedOption) {
        this.selectedOption = selectedOption;
    }

    @Override
    @Card.Type
    public int execute(Context context) {
        HomeFragment.setFeedRestoKind(context, MENU_TO_FORWARD.get(selectedOption));
        return Card.Type.RESTO;
    }

    @Override
    @Card.Type
    public int undo(Context context) {
        HomeFragment.setFeedRestoKind(context, MENU_TO_REVERSE.get(selectedOption));
        return Card.Type.RESTO;
    }

    @Override
    public int getCompleteMessage() {
        return MENU_TO_ITEM.get(selectedOption);
    }
}
