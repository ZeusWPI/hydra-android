/*
 * Copyright (C) 2015 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.ugent.zeus.hydra.common.ui.recyclerview.ordering;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Interface to listen for a move or dismissal event from a {@link ItemTouchHelper.Callback}.
 * This interface only handles dragging, not swiping.
 *
 * @author Paul Burke (ipaulpro)
 * @author Niko Strijbol
 */
public interface ItemDragHelperAdapter {

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and <strong>not</strong> at the end of a "drop" event.<br>
     * <br>
     * Implementations should call {@link RecyclerView.Adapter#notifyItemMoved(int, int)} after
     * adjusting the underlying data to reflect this move.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then resolved position of the moved item.
     * @return True if the item was moved to the new adapter position.
     *
     * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
     * @see RecyclerView.ViewHolder#getAdapterPosition()
     */
    boolean onItemMove(int fromPosition, int toPosition);

    /**
     * Called when the item has been moved and everything is finished.
     *
     * @param viewHolder The moved view holder.
     *
     * @see ItemTouchHelper.Callback#clearView(RecyclerView, RecyclerView.ViewHolder)
     */
    void onMoveCompleted(RecyclerView.ViewHolder viewHolder);

    /**
     * Enables the adapter to temporarily disable dragging by long pressing.
     *
     * @return True if long pressing is enabled.
     *
     * @see ItemTouchHelper.Callback#isLongPressDragEnabled()
     */
    boolean isLongPressDragEnabled();
}
