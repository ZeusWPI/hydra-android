package be.ugent.zeus.hydra.repository.extensions;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.repository.Result;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.Adapter;

import java.util.List;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class RecyclerHelper<D> extends Plugin implements Observer<Result<List<D>>> {

    private static final String TAG = "RequestPlugin";
    private Adapter<D, ?> adapter;
    private RecyclerView recyclerView;

    public RecyclerHelper(Adapter<D, ?> adapter) {
        this.adapter = adapter;
    }

    public RecyclerHelper() {}

    @Override
    protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = $(view, R.id.recycler_view);
        if (adapter == null) {
            throw new IllegalStateException("You must set an adapter before onViewCreated is called.");
        }
        recyclerView.setAdapter(adapter);
    }

    public void receiveData(List<D> data) {
        if (adapter != null) {
            adapter.setItems(data);
        }
    }

    /**
     * Set the adapter. The implementation of {@link #onViewCreated(View, Bundle)} implies that this function must
     * be called before onViewCreated is called. Not respecting this order may lead to undefined behavior.
     *
     * @param adapter The adapter to use.
     */
    public void setAdapter(Adapter<D, ?> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onChanged(@Nullable Result<List<D>> listResult) {

        Log.d(TAG, "onChanged: receiving data");

        if (listResult == null) {
            adapter.clear();
            return;
        }

        if (listResult.getStatus() == Result.Status.DONE || listResult.getStatus() == Result.Status.CONTINUING) {
            adapter.setItems(listResult.getData());
            return;
        }

        if (listResult.getStatus() == Result.Status.ERROR) {
            if (listResult.hasData()) {
                adapter.setItems(listResult.getData());
            }
            // DO error
            showError(listResult.getError());
        }
    }

    private void showError(Throwable throwable) {
        Context c = getHost().getContext();
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(getHost().getRoot(), c.getString(R.string.failure), Snackbar.LENGTH_LONG)
                //.setAction(c.getString(R.string.again), v -> refresh())
                .show();
    }

    public void hideRecyclerView() {
        recyclerView.setVisibility(View.GONE);
    }

    public void showRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decoration) {
        recyclerView.addItemDecoration(decoration);
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
