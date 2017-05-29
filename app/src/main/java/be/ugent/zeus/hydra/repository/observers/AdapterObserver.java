package be.ugent.zeus.hydra.repository.observers;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;
import be.ugent.zeus.hydra.repository.Result;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.Adapter;

import java.util.List;

/**
 * Enables using a {@link Adapter} as {@link Observer}. This observer handles setting the data, and only that. It does
 * not handle errors, or anything like that.
 *
 * @author Niko Strijbol
 */
public class AdapterObserver<D> implements Observer<Result<List<D>>> {

    private static final String TAG = "AdapterObserver";

    private final Adapter<D, ?> adapter;

    /**
     * @param adapter The adapter.
     */
    public AdapterObserver(Adapter<D, ?> adapter) {
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
            } else {
                adapter.clear();
            }
        }
    }
}