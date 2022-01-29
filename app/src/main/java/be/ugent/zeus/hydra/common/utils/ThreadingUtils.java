/*
 * Copyright (c) 2022 Niko Strijbol
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

import androidx.annotation.MainThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * TODO: check if we need to cache the thread executor?
     *
     * @param code The code to run.
     */
    @MainThread
    public static void execute(Runnable code) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(code);
        executor.shutdown();
    }
}
