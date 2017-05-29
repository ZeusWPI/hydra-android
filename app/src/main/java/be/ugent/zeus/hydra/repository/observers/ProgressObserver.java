package be.ugent.zeus.hydra.repository.observers;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.repository.Result;

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
        if (result == null || result.getStatus() == Result.Status.ERROR || result.getStatus() == Result.Status.DONE) {
            progressBar.setVisibility(View.GONE);
        }
    }
}