package be.ugent.zeus.hydra.plugins;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.caching.CacheableRequest;
import be.ugent.zeus.hydra.loaders.LoaderPlugin;
import be.ugent.zeus.hydra.loaders.LoaderProvider;
import be.ugent.zeus.hydra.recyclerview.adapters.common.Adapter;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.common.SimpleCacheRequest;
import java8.util.function.BiFunction;
import java8.util.function.Consumer;

import java.io.Serializable;
import java.util.List;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Plugin extending the RequestPlugin to provide a default implementation for use with a {@link RecyclerView} and
 * a {@link Adapter}.
 *
 * Like it's parent, when using a request that should be cached, you should use the {@link #cached(CacheableRequest, Adapter)}
 * factory method. It doesn't really matter if you call the method on this class or it's parent, as it's the same.
 *
 * @author Niko Strijbol
 */
public class RecyclerViewPlugin<D, E extends List<D>> extends RequestPlugin<E> {

    private Adapter<D, ?> adapter;
    private RecyclerView recyclerView;

    public RecyclerViewPlugin(BiFunction<Context, Boolean, Request<E>> provider, @Nullable Adapter<D, ?> adapter) {
        super(provider);
        this.adapter = adapter;
        super.setDataCallback(this::receiveData);
    }

    public RecyclerViewPlugin(LoaderProvider<E> provider, @Nullable Adapter<D, ?> adapter) {
        super(provider);
        this.adapter = adapter;
        super.setDataCallback(this::receiveData);
    }

    /**
     * Instantiate a RequestPlugin with a cacheable request, and effectively caching the request.
     *
     * The sole reason this is a static method is because Java's type system is not strong enough to enforce
     * Serializable on the type parameter for one constructor and not the others.
     *
     * @param request The request to use.
     * @param <D>     The type parameter of the request.
     *
     * @return An instance of RequestPlugin.
     */
    public static <D, E extends List<D> & Serializable> RecyclerViewPlugin<D, E> cached(CacheableRequest<E> request,  @Nullable Adapter<D, ?> adapter) {
        return new RecyclerViewPlugin<>((c, b) -> new SimpleCacheRequest<>(c, request, b), adapter);
    }

    @Override
    protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = $(view, R.id.recycler_view);
        if (adapter == null) {
            throw new IllegalStateException("You must set an adapter before onViewCreated is called.");
        }
        recyclerView.setAdapter(adapter);
    }

    @Override
    public LoaderPlugin<E> setDataCallback(Consumer<E> dataConsumer) {
        return super.setDataCallback(ds -> {
            receiveData(ds);
            dataConsumer.accept(ds);
        });
    }

    public void receiveData(E data) {
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

    public RecyclerView getRecyclerView() {
        return recyclerView;
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