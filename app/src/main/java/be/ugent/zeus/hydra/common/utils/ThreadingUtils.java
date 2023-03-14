/*
 * Copyright (c) 2023 Niko Strijbol
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
package be.ugent.zeus.hydra.common.utils;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Contains some utilities to run code in another thread.
 * A very basic replacement for some AsyncTask usage.
 *
 * @author Niko Strijbol
 */
public class ThreadingUtils {

    /**
     * Runs some code in the background.
     *
     * @param code The code to run on a worker thread.
     */
    @MainThread
    public static void execute(Runnable code) {
        execute(code, null);
    }

    /**
     * Run some code in the background.
     *
     * @param code           The code to run.
     * @param afterExecution Runs in the UI thread, after execution has completed.
     */
    @MainThread
    public static void execute(@NonNull @WorkerThread Runnable code, @Nullable @MainThread Runnable afterExecution) {
        executeWithResult(() -> {
            code.run();
            return null;
        }, unused -> {
            if (afterExecution != null) {
                afterExecution.run();
            }
        });
    }

    /**
     * Run a piece of code on a background thread.
     *
     * @param code           The code to run.
     * @param afterExecution Run on the main thread after the code has run.
     * @param <R>            The type of the result.
     */
    @MainThread
    public static <R> void executeWithResult(@NonNull @WorkerThread Supplier<R> code, @Nullable @MainThread Consumer<R> afterExecution) {
        executeWithProgress(consumer -> {
            R result = code.get();
            consumer.accept(result);
        }, afterExecution);
    }

    /**
     * Run a piece of code on a background thread.
     *
     * @param code             The code to run. The argument is a function to publish progress.
     * @param progressListener Receives progress updates on the main thread.
     * @param <R>              The type of the result.
     */
    @MainThread
    public static <R> void executeWithProgress(@NonNull Consumer<Consumer<R>> code, @Nullable @MainThread Consumer<R> progressListener) {
        // TODO: check if we need to cache the thread executor?
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            Consumer<R> progressReceiver;
            if (progressListener != null) {
                progressReceiver = r -> handler.post(() -> progressListener.accept(r));
            } else {
                progressReceiver = r -> {
                };
            }
            code.accept(progressReceiver);
        });
        executor.shutdown();
    }
}
