/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.common.arch.observers;

import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

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