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

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import java.util.function.Consumer;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * Calls the listener if the result has no data and only an exception.
 * <p>
 * This differs from {@link PartialErrorObserver}, which will always call the listener if there is an exception, even
 * if there is data also.
 *
 * @author Niko Strijbol
 */
public abstract class ErrorObserver<D> implements Observer<Result<D>> {

    public static <D> ErrorObserver<D> with(Consumer<RequestException> consumer) {
        return new ErrorObserver<>() {
            @Override
            protected void onError(RequestException throwable) {
                consumer.accept(throwable);
            }
        };
    }

    @Override
    public void onChanged(@Nullable Result<D> e) {
        if (e != null && !e.hasData()) {
            onError(e.error());
        }
    }

    protected abstract void onError(RequestException throwable);
}