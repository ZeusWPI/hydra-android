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

package be.ugent.zeus.hydra.feed.cards.resto;

import android.content.Context;
import android.util.SparseIntArray;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.widgets.MenuTable;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.commands.FeedCommand;
import be.ugent.zeus.hydra.feed.preferences.HomeFragment;

import static be.ugent.zeus.hydra.feed.cards.resto.RestoCardViewHolder.KindMenu.*;

/**
 * Command that saves and reverses changes to what is displayed in the menu.
 *
 * @author Niko Strijbol
 */
class RestoKindCommand implements FeedCommand {

    /**
     * Maps menu items to their message of completion, i.e. the message shown when the action has been performed.
     */
    private static final SparseIntArray MENU_TO_ITEM = new SparseIntArray();

    static {
        MENU_TO_ITEM.append(HIDE_SOUP, R.string.feed_card_hidden_menu_soup);
        MENU_TO_ITEM.append(SHOW_SOUP, R.string.feed_card_shown_menu_soup);
        MENU_TO_ITEM.append(HIDE_HOT, R.string.feed_card_hidden_menu_hot);
        MENU_TO_ITEM.append(SHOW_HOT, R.string.feed_card_shown_menu_hot);
        MENU_TO_ITEM.append(HIDE_COLD, R.string.feed_card_hidden_menu_cold);
        MENU_TO_ITEM.append(SHOW_COLD, R.string.feed_card_shown_menu_cold);
    }

    @RestoCardViewHolder.KindMenu
    private final int selectedOption;

    RestoKindCommand(@RestoCardViewHolder.KindMenu int selectedOption) {
        this.selectedOption = selectedOption;
    }

    @Override
    @Card.Type
    public int execute(Context context) {
        int flag = commandToFlag(selectedOption);
        // The "flag" is negative, so we want to hide it.
        if (flag < 0) {
            // Convert the flag back to an actual flag before saving it.
            HomeFragment.removeFlag(context, -flag);
        } else {
            HomeFragment.addFlag(context, flag);
        }
        return Card.Type.RESTO;
    }

    @Override
    @Card.Type
    public int undo(Context context) {
        int flag = commandToFlag(selectedOption);
        // The "flag" is negative, so we wanted to hide it, but show it again now.
        if (flag < 0) {
            // Convert the flag back to an actual flag before saving it.
            HomeFragment.addFlag(context, -flag);
        } else {
            HomeFragment.removeFlag(context, flag);
        }
        return Card.Type.RESTO;
    }

    @Override
    public int completeMessage() {
        return MENU_TO_ITEM.get(selectedOption);
    }
    
    // Get the flag for display kind, with negative flags indicating hiding.
    private int commandToFlag(@RestoCardViewHolder.KindMenu int command) {
        return switch (command) {
            case SHOW_HOT -> MenuTable.DisplayKind.HOT;
            case HIDE_HOT -> -MenuTable.DisplayKind.HOT;
            case SHOW_COLD -> MenuTable.DisplayKind.COLD;
            case HIDE_COLD -> -MenuTable.DisplayKind.COLD;
            case SHOW_SOUP -> MenuTable.DisplayKind.SOUP;
            case HIDE_SOUP -> -MenuTable.DisplayKind.SOUP;
            default -> throw new IllegalStateException("Unknown resto menu card command...");
        };
    }
}
