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

package be.ugent.zeus.hydra.wpi.tab.requests;

import android.view.ViewGroup;
import androidx.annotation.NonNull;

import java.util.function.Consumer;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DiffAdapter;
import be.ugent.zeus.hydra.common.utils.ViewUtils;

/**
 * @author Niko Strijbol
 */
public class TabRequestsAdapter extends DiffAdapter<TabRequest, AcceptableRequestsViewHolder> {

    private final Consumer<TabRequest> acceptor;
    private final Consumer<TabRequest> decliner;

    public TabRequestsAdapter(Consumer<TabRequest> acceptor, Consumer<TabRequest> decliner) {
        setHasStableIds(true);
        this.acceptor = acceptor;
        this.decliner = decliner;
    }

    @Override
    public long getItemId(int position) {
        return item(position).id();
    }

    @NonNull
    @Override
    public AcceptableRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        return new AcceptableRequestsViewHolder(ViewUtils.inflate(p, R.layout.item_tab_request), acceptor, decliner);
    }
}
