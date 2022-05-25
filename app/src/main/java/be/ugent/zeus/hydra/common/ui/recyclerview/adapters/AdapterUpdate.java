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

package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import androidx.annotation.AnyThread;
import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Represents a data update for an {@link DiffAdapter}.
 */
interface AdapterUpdate<D> {

    /**
     * Returns the new data. If you need to calculations, this is the place. Guaranteed to be called before
     * {@link #applyUpdatesTo(ListUpdateCallback)} is called.
     *
     * @param existingData The existing data. Will no longer be used after this call. Null if there is no existing data.
     * @return The new data. May be the same as the existing data. Null if no data is available.
     */
    @Nullable
    @AnyThread
    List<D> getNewData(@Nullable List<D> existingData);

    /**
     * Apply the update to the update callback. At this point, the underlying data of the callback is already
     * updated to reflect the new data.
     *
     * @param listUpdateCallback The callback to update.
     */
    @MainThread
    void applyUpdatesTo(ListUpdateCallback listUpdateCallback);

    /**
     * @return False if multithreading should not be used.
     */
    default boolean shouldUseMultiThreading() {
        return true;
    }
}
