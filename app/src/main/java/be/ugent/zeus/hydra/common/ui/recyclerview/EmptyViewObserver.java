package be.ugent.zeus.hydra.common.ui.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java8.util.stream.StreamSupport;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class that manages displaying and hiding the RecyclerView and the empty View.
 *
 * If the adapter has no data, the View will displayed. If there is data, the RecyclerView will be displayed.
 *
 * @author Niko Strijbol
 */
public class EmptyViewObserver extends RecyclerView.AdapterDataObserver {

    private final RecyclerView recyclerView;
    private final View emptyView;
    private final AdapterConsolidator consolidator;

    /**
     * When using this constructor, you must register the adapter with the RecyclerView before calling this.
     *
     * @param recyclerView The RecyclerView. Will be shown when there is data.
     * @param emptyView The View to display when there is no data.
     */
    public EmptyViewObserver(RecyclerView recyclerView, View emptyView) {
        this(recyclerView, emptyView, new AdapterConsolidator(recyclerView.getAdapter()));
    }

    /**
     * The RecyclerView must have access to the adapter before data events begin. This means you must have called
     * {@link RecyclerView#setAdapter(RecyclerView.Adapter)} before calling
     * {@link RecyclerView.Adapter#registerAdapterDataObserver(RecyclerView.AdapterDataObserver)}.
     *
     * @param recyclerView The RecyclerView. Will be shown when there is data.
     * @param emptyView The View to display when there is no data.
     */
    public EmptyViewObserver(RecyclerView recyclerView, View emptyView, AdapterConsolidator consolidator) {
        this.recyclerView = recyclerView;
        this.emptyView = emptyView;
        this.consolidator = consolidator;
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

    private void showHide() {
        if (consolidator.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    public static class AdapterConsolidator {

        private Collection<RecyclerView.Adapter<?>> adapters = new ArrayList<>();

        public AdapterConsolidator(RecyclerView.Adapter<?> baseAdapter) {
            adapters.add(baseAdapter);
        }

        boolean isEmpty() {
            return StreamSupport.stream(adapters).allMatch(a -> a.getItemCount() == 0);
        }

        public AdapterConsolidator add(RecyclerView.Adapter<?> adapter) {
            adapters.add(adapter);
            return this;
        }
    }
}