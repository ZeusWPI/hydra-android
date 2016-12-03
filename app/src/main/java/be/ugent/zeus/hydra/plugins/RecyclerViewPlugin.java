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
import be.ugent.zeus.hydra.loaders.DataCallback;
import be.ugent.zeus.hydra.loaders.LoaderProvider;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.recyclerview.adapters.common.Adapter;

import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class RecyclerViewPlugin<D, E extends List<D>> extends Plugin implements DataCallback<E> {

    private static final String TAG = "RecyclerViewPlugin";

    private final RequestPlugin<E> requestPlugin;
    private Adapter<D, ?> adapter;
    private RecyclerView recyclerView;

    private boolean adapterSet;

    private DataCallback<E> callback;

    /**
     * Note: if you need caching for a {@link CacheableRequest}, you can use the function {@link RequestPlugin#wrap(CacheableRequest)},
     * which will construct a RequestProvider for a CacheableRequest that utilises caching.
     *
     * @param adapter The adapter.
     * @param provider The request provider.
     */
    public RecyclerViewPlugin(RequestPlugin.RequestProvider<E> provider, @Nullable Adapter<D, ?> adapter) {
        this.requestPlugin = new RequestPlugin<>(this, provider);
        this.adapter = adapter;
    }

    public RecyclerViewPlugin(LoaderProvider<E> provider, @Nullable Adapter<D, ?> adapter) {
        this.requestPlugin = new RequestPlugin<>(this, provider);
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
        if(adapter == null) {
            Log.w(TAG, "No adapter was set, so nothing will happen.");
        }
        recyclerView.setAdapter(adapter);
        adapterSet = true;
    }

    @Override
    public void receiveData(@NonNull E data) {
        adapter.setItems(data);
        if(callback != null) {
            callback.receiveData(data);
        }
    }

    @Override
    public void receiveError(@NonNull Throwable e) {
        Log.e(TAG, "Error while getting data.", e);
        Snackbar.make(getHost().getRoot(), getHost().getContext().getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getHost().getContext().getString(R.string.again), v -> requestPlugin.refresh())
                .show();
        if(callback != null) {
            callback.receiveError(e);
        }
    }

    public void setAdapter(Adapter<D, ?> adapter) {
        if(adapterSet) {
            throw new IllegalStateException("The adapter must be set before onViewCreated()");
        }
        this.adapter = adapter;
    }

    public RequestPlugin<E> getRequestPlugin() {
        return requestPlugin;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setCallback(DataCallback<E> callback) {
        this.callback = callback;
    }

    public void addItemDecoration(RecyclerView.ItemDecoration decoration) {
        recyclerView.addItemDecoration(decoration);
    }

    public void hideRecyclerView() {
        recyclerView.setVisibility(View.GONE);
    }

    public void showRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
    }
}