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

import android.view.ViewGroup;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DiffAdapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

/**
 * @author Niko Strijbol
 */
class LineupAdapter extends DiffAdapter<ArtistOrTitle, DataViewHolder<ArtistOrTitle>> {

    private static final int VIEW_TYPE_ARTIST = 0;
    private static final int VIEW_TYPE_TITLE = 1;

    @NonNull
    @Override
    public DataViewHolder<ArtistOrTitle> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_ARTIST:
                return new ArtistViewHolder(ViewUtils.inflate(parent, R.layout.item_sko_lineup_artist));
            case VIEW_TYPE_TITLE:
                return new TitleViewHolder(ViewUtils.inflate(parent, R.layout.item_title));
            default:
                throw new IllegalStateException("Unknown view type.");
        }

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isArtist() ? VIEW_TYPE_ARTIST : VIEW_TYPE_TITLE;
    }
}
