package be.ugent.zeus.hydra.common.arch.observers;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import java.util.List;

import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DataAdapter;

/**
 * Enables using a {@link DataAdapter} as {@link Observer}. This observer handles setting the data, and only that. It does
 * not handle errors, or anything like that.
 *
 * @author Niko Strijbol
 */
public class AdapterObserver<D> extends SuccessObserver<List<D>> {

    private final DataAdapter<D, ?> dataAdapter;

    public AdapterObserver(DataAdapter<D, ?> adapter) {
        this.dataAdapter = adapter;
    }

    @Override
    protected void onSuccess(@NonNull List<D> data) {
        dataAdapter.submitData(data);
    }

    @Override
    protected void onEmpty() {
        dataAdapter.clear();
    }
}