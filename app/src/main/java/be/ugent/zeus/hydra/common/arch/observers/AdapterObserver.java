package be.ugent.zeus.hydra.common.arch.observers;

import android.arch.lifecycle.Observer;

import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.Adapter;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.Adapter2;

import java.util.List;

/**
 * Enables using a {@link Adapter2} as {@link Observer}. This observer handles setting the data, and only that. It does
 * not handle errors, or anything like that.
 *
 * @author Niko Strijbol
 */
public class AdapterObserver<D> extends SuccessObserver<List<D>> {

    private final Adapter<D, ?> adapter;
    private final Adapter2<D, ?> adapter2;

    /**
     * @param adapter The adapter.
     */
    @Deprecated
    public AdapterObserver(Adapter<D, ?> adapter) {
        this.adapter = adapter;
        this.adapter2 = null;
    }

    public AdapterObserver(Adapter2<D, ?> adapter) {
        this.adapter = null;
        this.adapter2 = adapter;
    }

    @Override
    protected void onSuccess(List<D> data) {
        if (adapter != null) {
            adapter.setItems(data);
        } else {
            assert adapter2 != null;
            adapter2.submitList(data);
        }
    }

    @Override
    protected void onEmpty() {
        if (adapter != null) {
            adapter.clear();
        } else {
            assert adapter2 != null;
            adapter2.clear();
        }
    }
}