package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * Adapted from {@link android.support.v7.util.AdapterListUpdateCallback}.
 *
 * @author Niko Strijbol
 */
class AdapterListUpdateCallback implements ListUpdateCallback {

    @NonNull
    private final RecyclerView.Adapter adapter;

    /**
     * Creates an AdapterListUpdateCallback that will dispatch update events to the given adapter.
     *
     * @param adapter The Adapter to send updates to.
     */
    AdapterListUpdateCallback(@NonNull RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onInserted(int position, int count) {
        adapter.notifyItemRangeInserted(position, count);
    }

    @Override
    public void onRemoved(int position, int count) {
        adapter.notifyItemRangeRemoved(position, count);
    }

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onChanged(int position, int count, Object payload) {
        adapter.notifyItemRangeChanged(position, count, payload);
    }

    @Override
    public void onDataSetChanged() {
        adapter.notifyDataSetChanged();
    }
}