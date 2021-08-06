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

package be.ugent.zeus.hydra.feed.cards.resto;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.widgets.MenuTable;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.feed.preferences.HomeFragment;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.menu.RestoFragment;

import static android.view.Menu.NONE;

/**
 * Home feed view holder for the resto menu.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class RestoCardViewHolder extends CardViewHolder {

    private final MenuTable table;

    public RestoCardViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        table = v.findViewById(R.id.menu_table);
    }

    @Override
    public void populate(Card card) {
        super.populate(card);

        RestoMenuCard menuCard = card.checkCard(Card.Type.RESTO);
        RestoMenu menu = menuCard.getRestoMenu();
        RestoChoice choice = menuCard.getRestoChoice();
        String text = itemView.getResources().getString(R.string.feed_resto_menu_title);
        toolbar.setTitle(String.format(text, DateUtils.getFriendlyDate(toolbar.getContext(), menu.getDate()), choice.getName()));

        // Get the mode.
        @MenuTable.DisplayKind
        int mode = HomeFragment.getFeedRestoKind(itemView.getContext());

        table.setMenu(menu, mode);

        // click listener
        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(itemView.getContext(), MainActivity.class);
            intent.putExtra(MainActivity.ARG_TAB, R.id.drawer_resto);
            intent.putExtra(RestoFragment.ARG_DATE, menu.getDate());
            itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public void onCreateMenu(Menu menu) {
        super.onCreateMenu(menu);

        @HomeFragment.FeedRestoKind
        String kind = HomeFragment.getFeedRestoKindRaw(itemView.getContext());

        // The logic below represent the fact that we always want one thing in the menu: the soup, the main course
        // or both, but never none.
        switch (kind) {
            case HomeFragment.FeedRestoKind.ALL:
                menu.add(NONE, KindMenu.HIDE_MAIN, NONE, R.string.feed_pref_resto_hide_main);
                menu.add(NONE, KindMenu.HIDE_SOUP, NONE, R.string.feed_pref_resto_hide_soup);
                break;
            case HomeFragment.FeedRestoKind.MAIN:
                // Main course is set, so offer to show the soup.
                menu.add(NONE, KindMenu.SHOW_SOUP, NONE, R.string.feed_pref_resto_show_soup);
                break;
            case HomeFragment.FeedRestoKind.SOUP:
                menu.add(NONE, KindMenu.SHOW_MAIN, NONE, R.string.feed_pref_resto_show_main);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case KindMenu.HIDE_MAIN:
            case KindMenu.HIDE_SOUP:
            case KindMenu.SHOW_MAIN:
            case KindMenu.SHOW_SOUP:
                adapter.getCompanion().executeCommand(new RestoKindCommand(item.getItemId()));
                return true;
            default:
                return super.onMenuItemClick(item);
        }
    }

    @interface KindMenu {
        int HIDE_SOUP = 1;
        int HIDE_MAIN = 2;
        int SHOW_SOUP = 3;
        int SHOW_MAIN = 4;
    }
}
