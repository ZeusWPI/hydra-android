package be.ugent.zeus.hydra.common.arch.observers;

import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * @author Niko Strijbol
 */
public class ProgressObserver<D> implements Observer<Result<D>> {

    private final ProgressBar progressBar;

    public ProgressObserver(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    @Override
    public void onChanged(@Nullable Result<D> result) {
        if (result == null || result.isDone()) {
            progressBar.setVisibility(View.GONE);
        }
    }
}