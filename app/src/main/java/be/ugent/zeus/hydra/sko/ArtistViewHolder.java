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

package be.ugent.zeus.hydra.sko;

import android.content.Intent;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.VisibleForTesting;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import com.squareup.picasso.Picasso;

/**
 * View holder for an {@link Artist}.
 * <p>
 * This class implements the base class with {@link ArtistOrTitle}, since it would be a lot of work to allow us to
 * have heterogeneous view holders in the adapters for little gain.
 *
 * @author Niko Strijbol
 */
class ArtistViewHolder extends DataViewHolder<ArtistOrTitle> implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

    @VisibleForTesting
    static final int MENU_ID_ADD_TO_CALENDAR = 0;

    private final TextView title;
    private final TextView date;
    private final ImageView image;
    private Artist artist;

    ArtistViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);
        image = itemView.findViewById(R.id.card_image);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void populate(ArtistOrTitle artistOrTitle) {
        this.artist = artistOrTitle.getArtist();
        title.setText(artist.getName());
        date.setText(artist.getDisplayDate(date.getContext()));
        Picasso.get().load(artist.getImage()).into(image);
        itemView.setOnClickListener(v -> {
            Intent intent = ArtistDetailsActivity.start(v.getContext(), artist);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (artist != null) {
            String string = itemView.getContext().getString(R.string.action_add_to_calendar);
            menu.add(Menu.NONE, MENU_ID_ADD_TO_CALENDAR, 0, string).setOnMenuItemClickListener(this);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        if (artist != null && item.getItemId() == MENU_ID_ADD_TO_CALENDAR) {
            Intent calendarIntent = artist.addToCalendarIntent();
            title.getContext().startActivity(calendarIntent);
            return true;
        }

        return false;
    }
}
