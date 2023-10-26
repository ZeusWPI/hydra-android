/*
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

package be.ugent.zeus.hydra.common.ui.recyclerview.headers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

/**
 * An {@link RecyclerView.Adapter} that only displays a header.
 * <p>
 * This adapter only has one element to display: a user-provided string.
 * <p>
 * In most cases, you probably want to use {@link #makeHideable(int, RecyclerView.Adapter)}.
 * This will create an adapter with header, but the header will be hidden when there are
 * no items in the item adapter.
 * <p>
 * Alternatively, you can manually use it with a {@link androidx.recyclerview.widget.ConcatAdapter},
 * allowing to add titles to other adapters without complicating those.
 *
 * @author Niko Strijbol
 */
public class HeaderAdapter extends RecyclerView.Adapter<HeaderAdapter.HeaderViewHolder> {

    private boolean hidden = false;

    private final String rawString;
    @StringRes
    private final int stringRes;

    /**
     * @param string The string resource to display as header.
     */
    public HeaderAdapter(@StringRes int string) {
        this.rawString = null;
        this.stringRes = string;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public HeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HeaderViewHolder(ViewUtils.inflate(parent, R.layout.item_header));
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderViewHolder holder, int position) {
        if (rawString == null) {
            holder.textView.setText(stringRes);
        } else {
            holder.textView.setText(rawString);
        }
    }

    @Override
    public int getItemCount() {
        if (hidden) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public long getItemId(int position) {
        if (rawString == null) {
            return stringRes;
        } else {
            return rawString.hashCode();
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        HeaderViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.header_text);
        }
    }

    private void setHidden(boolean hidden) {
        boolean wasHidden = this.hidden;
        if (wasHidden == hidden) {
            return;
        }
        this.hidden = hidden;

        if (wasHidden) {
            // The header was hidden, but is now shown.
            notifyItemInserted(0);
        } else {
            // The header was shown, but is now hidden.
            notifyItemRemoved(0);
        }
    }

    /**
     * Return an adapter that hides the header if there are no items.
     * <p>
     * This method basically listens to the item adapter, and removes the
     * header if the item adapter is empty; otherwise it will be shown.
     *
     * @param headerAdapter The header adapter to use.
     * @param itemAdapter   The item adapter to use.
     */
    public static RecyclerView.Adapter<? extends RecyclerView.ViewHolder> makeHideable(HeaderAdapter headerAdapter, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> itemAdapter) {

        // Check the config to see if we support stable ids.
        ConcatAdapter.Config config;
        if (itemAdapter.hasStableIds()) {
            config = new ConcatAdapter.Config.Builder().setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS).build();
        } else {
            config = ConcatAdapter.Config.DEFAULT;
        }

        itemAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            private void syncHeader(int itemCount) {
                headerAdapter.setHidden(itemCount == 0);
            }

            @Override
            public void onChanged() {
                syncHeader(itemAdapter.getItemCount());
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                syncHeader(itemAdapter.getItemCount() + itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                syncHeader(itemAdapter.getItemCount() - itemCount);
            }
        });

        // Set the initial state correctly.
        headerAdapter.setHidden(itemAdapter.getItemCount() == 0);

        return new ConcatAdapter(config, headerAdapter, itemAdapter);
    }

    /**
     * @see #makeHideable(HeaderAdapter, RecyclerView.Adapter)
     */
    public static RecyclerView.Adapter<? extends RecyclerView.ViewHolder> makeHideable(@StringRes int header, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> itemAdapter) {
        return makeHideable(new HeaderAdapter(header), itemAdapter);
    }
}
