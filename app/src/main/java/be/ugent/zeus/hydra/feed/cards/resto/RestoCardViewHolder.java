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
        String text = itemView.getResources().getString(R.string.feed_resto_menu_title);
        toolbar.setTitle(String.format(text, DateUtils.getFriendlyDate(toolbar.getContext(), menu.getDate())));

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

        // We always want at least one thing in the menu.
        // So, if we only have one thing, the last item won't have the hide menu.
        @MenuTable.DisplayKind
        int kind = HomeFragment.getFeedRestoKind(itemView.getContext());
        
        int displayed = Integer.bitCount(kind);
        
        if ((kind & MenuTable.DisplayKind.SOUP) == MenuTable.DisplayKind.SOUP) {
            if (displayed > 1) {
                menu.add(NONE, KindMenu.HIDE_SOUP, NONE, R.string.feed_pref_resto_hide_soup);
            }
        } else {
            menu.add(NONE, KindMenu.SHOW_SOUP, NONE, R.string.feed_pref_resto_show_soup);
        }

        if ((kind & MenuTable.DisplayKind.HOT) == MenuTable.DisplayKind.HOT) {
            if (displayed > 1) {
                menu.add(NONE, KindMenu.HIDE_HOT, NONE, R.string.feed_pref_resto_hide_hot);
            }
        } else {
            menu.add(NONE, KindMenu.SHOW_HOT, NONE, R.string.feed_pref_resto_show_hot);
        }

        if ((kind & MenuTable.DisplayKind.COLD) == MenuTable.DisplayKind.COLD) {
            if (displayed > 1) {
                menu.add(NONE, KindMenu.HIDE_COLD, NONE, R.string.feed_pref_resto_hide_cold);
            }
        } else {
            menu.add(NONE, KindMenu.SHOW_COLD, NONE, R.string.feed_pref_resto_show_cold);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case KindMenu.HIDE_HOT:
            case KindMenu.HIDE_SOUP:
            case KindMenu.SHOW_HOT:
            case KindMenu.SHOW_SOUP:
            case KindMenu.SHOW_COLD:
            case KindMenu.HIDE_COLD:
                adapter.getCompanion().executeCommand(new RestoKindCommand(item.getItemId()));
                return true;
            default:
                return super.onMenuItemClick(item);
        }
    }

    @interface KindMenu {
        int HIDE_SOUP = 1;
        int HIDE_HOT = 2;
        int SHOW_SOUP = 3;
        int SHOW_HOT = 4;
        int HIDE_COLD = 5;
        int SHOW_COLD = 6;
    }
}
