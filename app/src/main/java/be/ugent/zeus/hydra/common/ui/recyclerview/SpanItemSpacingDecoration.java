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

package be.ugent.zeus.hydra.common.ui.recyclerview;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import be.ugent.zeus.hydra.R;

/**
 * Add spacing to elements for a staggered grid. This will add {@link #spacing} to the left and right of every element.
 * <p>
 * If the layout manager is not a {@link StaggeredGridLayoutManager} or {@link GridLayoutManager}, this will do nothing.
 *
 * @author Niko Strijbol
 */
public class SpanItemSpacingDecoration extends RecyclerView.ItemDecoration {

    private final int spacing;

    @SuppressWarnings("WeakerAccess")
    public SpanItemSpacingDecoration(int spacing) {
        this.spacing = spacing;
    }

    /**
     * Initialize the decoration using the default spacing.
     */
    public SpanItemSpacingDecoration(Context context) {
        this(context.getResources().getDimensionPixelSize(R.dimen.card_margin_big_component));
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull final View view, @NonNull final RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        //If this is not supported, do nothing. Otherwise extract the span.
        if (!(parent.getLayoutManager() instanceof StaggeredGridLayoutManager || parent.getLayoutManager() instanceof GridLayoutManager)) {
            return;
        }

        outRect.right = spacing;
        outRect.left = spacing;
    }
}
