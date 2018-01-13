package be.ugent.zeus.hydra.common.arch.observers;

import android.arch.lifecycle.Observer;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.Adapter;

import java.util.List;

/**
 * Enables using a {@link Adapter} as {@link Observer}. This observer handles setting the data, and only that. It does
 * not handle errors, or anything like that.
 *
 * @author Niko Strijbol
 */
public class AdapterObserver<D> extends SuccessObserver<List<D>> {

    private final Adapter<D, ?> adapter;

    /**
     * @param adapter The adapter.
     */
    public AdapterObserver(Adapter<D, ?> adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void onSuccess(List<D> data) {
        adapter.setItems(data);
    }

    @Override
    protected void onEmpty() {
        adapter.clear();
    }
}