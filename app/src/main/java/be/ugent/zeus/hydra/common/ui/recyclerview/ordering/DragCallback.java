package be.ugent.zeus.hydra.common.ui.recyclerview.ordering;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * @author Niko Strijbol
 */
public class DragCallback extends ItemTouchHelper.SimpleCallback {

    private final ItemDragHelperAdapter adapter;

    public DragCallback(ItemDragHelperAdapter adapter) {
        super(ItemTouchHelper.DOWN | ItemTouchHelper.UP, 0);
        this.adapter = adapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return adapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return adapter.isLongPressDragEnabled();
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        adapter.onMoveCompleted(viewHolder);
    }
}