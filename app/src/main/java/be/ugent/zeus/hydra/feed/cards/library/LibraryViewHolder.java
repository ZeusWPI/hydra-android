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

package be.ugent.zeus.hydra.feed.cards.library;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Optional;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.utils.ViewUtils;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.feed.commands.DisableTypeCommand;
import be.ugent.zeus.hydra.library.details.OpeningHours;

/**
 * View holder for the list of library opening hours.
 *
 * @author Niko Strijbol
 */
public class LibraryViewHolder extends CardViewHolder {

    private final LinearLayout list;

    private final int rowPadding;
    private final int styleName;
    private final int styleHours;

    public LibraryViewHolder(View itemView, HomeFeedAdapter adapter) {
        super(itemView, adapter);
        list = itemView.findViewById(R.id.library_list);

        Context c = itemView.getContext();
        styleName = ViewUtils.getAttr(c, R.attr.textAppearanceBodyMedium);
        styleHours = ViewUtils.getAttr(c, R.attr.textAppearanceCaption);
        rowPadding = c.getResources().getDimensionPixelSize(R.dimen.material_baseline_grid_1x);

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra(MainActivity.ARG_TAB, R.id.drawer_library);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public void populate(Card card) {
        super.populate(card);

        // Clear existing items that might still be in our list.
        list.removeAllViews();

        // Add the new items for the current card.
        LibraryCard libraryCard = card.checkCard(Card.Type.LIBRARY);
        int size = libraryCard.getLibraries().size();
        Context c = list.getContext();
        for (int i = 0; i < size; i++) {
            Pair<String, Result<Optional<OpeningHours>>> pair = libraryCard.getLibraries().get(i);
            addLibraryName(c, pair.first);
            addOpeningHour(c, pair.second, i == size - 1);
        }
    }

    private void addLibraryName(Context context, String name) {
        TextView v = new TextView(context, null, styleName);
        TableRow.LayoutParams textParam =
                new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), rowPadding / 2);
        v.setLayoutParams(textParam);
        v.setText(name);

        list.addView(v);
    }

    private void addOpeningHour(Context context, Result<Optional<OpeningHours>> result, boolean isLast) {
        TextView v = new TextView(context, null, styleHours);
        TableRow.LayoutParams textParam =
                new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        if (!isLast) {
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), rowPadding);
        }
        v.setLayoutParams(textParam);

        if (!result.hasData() || result.getData().isEmpty()) {
            v.setText(R.string.library_list_no_opening_hours);
        } else {
            v.setText(context.getString(R.string.library_list_opening_hours_today, result.getData().get().getHours()));
        }

        list.addView(v);
    }

    @Override
    public void onSwiped() {
        adapter.getCompanion().executeCommand(new DisableTypeCommand(Card.Type.LIBRARY));
    }

    @Override
    public boolean isSwipeEnabled() {
        return true;
    }
}
