package be.ugent.zeus.hydra.ui.common.plugins;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.ui.common.plugins.loader.LoaderCallback;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.Adapter;
import java8.util.function.BiFunction;
import java8.util.function.Consumer;
import java8.util.function.Consumers;
import java8.util.function.Function;

import java.util.List;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * Plugin extending the RequestPlugin to provide a default implementation for use with a {@link RecyclerView} and
 * a {@link Adapter}.
 *
 * @author Niko Strijbol
 */
public class RecyclerViewPlugin<D> extends RequestPlugin<List<D>> {

    private Adapter<D, ?> adapter;
    private RecyclerView recyclerView;

    public RecyclerViewPlugin(BiFunction<Context, Boolean, Request<List<D>>> provider, @Nullable Adapter<D, ?> adapter) {
        super(provider);
        this.adapter = adapter;
        super.setSuccessCallback(this::receiveData);
    }

    public RecyclerViewPlugin(LoaderCallback<List<D>> provider, @Nullable Adapter<D, ?> adapter) {
        super(provider);
        this.adapter = adapter;
        super.setSuccessCallback(this::receiveData);
    }

    public RecyclerViewPlugin(Function<Boolean, LoaderCallback<List<D>>> supplier, @Nullable Adapter<D, ?> adapter) {
        super(supplier);
        this.adapter = adapter;
        super.setSuccessCallback(this::receiveData);
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
    public void setSuccessCallback(Consumer<List<D>> dataConsumer) {
        super.setSuccessCallback(Consumers.andThen(this::receiveData, dataConsumer));
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