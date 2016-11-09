package be.ugent.zeus.hydra.plugins;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.caching.CacheableRequest;
import be.ugent.zeus.hydra.loaders.LoaderCallback;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.recyclerview.adapters.common.SimpleItemAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class RecyclerViewPlugin<D extends Serializable, E extends ArrayList<D>> extends Plugin implements LoaderCallback.DataCallbacks<E> {

    private static final String TAG = "RecyclerViewPlugin";

    private final RequestPlugin<E> requestPlugin;
    private final SimpleItemAdapter<D, ?> adapter;
    private RecyclerView recyclerView;

    public RecyclerViewPlugin(CacheableRequest<E> request, SimpleItemAdapter<D, ?> adapter) {
        requestPlugin = new RequestPlugin<>(this, request);
        this.adapter = adapter;
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        plugins.add(requestPlugin);
        super.onAddPlugins(plugins);
    }

    @Override
    protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = $(view, R.id.recycler_view);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void receiveData(@NonNull E data) {
        adapter.setItems(data);
    }

    @Override
    public void receiveError(@NonNull Throwable e) {
        Log.e(TAG, "Error while getting data.", e);
        Snackbar.make(getHost().getRoot(), getHost().getContext().getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getHost().getContext().getString(R.string.again), v -> requestPlugin.refresh())
                .show();
    }

    public RequestPlugin<E> getRequestPlugin() {
        return requestPlugin;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}