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

package be.ugent.zeus.hydra.library.list;

import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import java.util.Optional;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.RecycleViewHolder;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.details.LibraryDetailActivity;
import be.ugent.zeus.hydra.library.details.OpeningHours;

/**
 * @author Niko Strijbol
 */
class LibraryViewHolder extends DataViewHolder<Pair<Library, Boolean>> implements RecycleViewHolder,
        Observer<Result<Optional<OpeningHours>>> {

    private final LibraryListAdapter adapter;

    private final TextView title;
    private final TextView subtitle;
    private final ImageView favourite;
    private final TextView openingHours;

    private boolean visible;

    LibraryViewHolder(View itemView, LibraryListAdapter adapter) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        favourite = itemView.findViewById(R.id.library_favourite_image);
        openingHours = itemView.findViewById(R.id.opening_hours);
        this.adapter = adapter;
    }

    @Override
    public void populate(Pair<Library, Boolean> data) {
        visible = data.second;
        openingHours.setVisibility(visible ? View.VISIBLE : View.GONE);
        title.setText(data.first.getName());
        subtitle.setText(data.first.getCampus());
        itemView.setOnClickListener(v -> LibraryDetailActivity.launchActivity(v.getContext(), data.first));
        favourite.setVisibility(visible ? View.VISIBLE : View.GONE);
        adapter.registerListener(data.first, this);
    }

    @Override
    public void onViewRecycled() {
        visible = false;
        adapter.unregisterListener(this);
    }

    @Override
    public void onChanged(@Nullable Result<Optional<OpeningHours>> result) {
        if (result == null || !result.hasData() || !result.getData().isPresent()) {
            openingHours.setText(R.string.library_list_no_opening_hours);
        } else {
            OpeningHours hours = result.getData().get();
            if (visible) {
                openingHours.setText(openingHours.getContext()
                        .getString(R.string.library_list_opening_hours_today, hours.getHours()));
            }
        }
    }
}