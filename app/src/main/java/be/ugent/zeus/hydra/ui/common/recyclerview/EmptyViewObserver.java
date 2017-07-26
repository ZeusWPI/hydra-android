package be.ugent.zeus.hydra.ui.common.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Class that manages displaying and hiding the RecyclerView and the empty View.
 *
 * If the adapter has no data, the View will displayed. If there is data, the RecyclerView will be displayed.
 *
 * The RecyclerView must have access to the adapter before data events begin. This means you must have called
 * {@link RecyclerView#setAdapter(RecyclerView.Adapter)} before calling {@link RecyclerView.Adapter#registerAdapterDataObserver(RecyclerView.AdapterDataObserver)}.
 *
 * @author Niko Strijbol
 */
public class EmptyViewObserver extends RecyclerView.AdapterDataObserver {

    private final RecyclerView recyclerView;
    private final View emptyView;

    /**
     * @param recyclerView The RecyclerView. Will be shown when there is data.
     * @param emptyView The View to display when there is no data.
     */
    public EmptyViewObserver(RecyclerView recyclerView, View emptyView) {
        this.recyclerView = recyclerView;
        this.emptyView = emptyView;
    }

    @Override
    public void onChanged() {
        showHide();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        // This does not change the amount of items.
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        // This does not change the amount of items.
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        showHide();
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        showHide();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        showHide();
    }

    private boolean isEmpty() {
        if (recyclerView.getAdapter() == null) {
            throw new IllegalStateException("You must register the Adapter before registering this class.");
        } else {
            return recyclerView.getAdapter().getItemCount() == 0;
        }
    }

    private void showHide() {
        if (isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }
}