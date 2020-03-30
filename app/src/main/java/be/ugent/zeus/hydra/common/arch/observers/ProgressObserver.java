package be.ugent.zeus.hydra.common.arch.observers;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.databinding.XProgressBarBinding;

/**
 * Observes a progress bar and hides it once the data is loaded.
 *
 * @author Niko Strijbol
 */
public class ProgressObserver<D> implements Observer<Result<D>> {

    private final ProgressBar progressBar;

    public ProgressObserver(@NonNull ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public ProgressObserver(@NonNull XProgressBarBinding binding) {
        this.progressBar = binding.progressBar;
    }

    @Override
    public void onChanged(@Nullable Result<D> result) {
        if (result == null || result.isDone()) {
            progressBar.setVisibility(View.GONE);
        }
    }
}