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

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Dumb update, where no information about the data update is available.
 *
 * @author Niko Strijbol
 */
class DumbUpdate<D> implements AdapterUpdate<D> {

    private final List<D> newData;

    DumbUpdate(List<D> newData) {
        this.newData = newData;
    }

    @Nullable
    @Override
    public List<D> newData(@Nullable List<D> existingData) {
        return newData;
    }

    @Override
    public void applyUpdatesTo(ListUpdateCallback listUpdateCallback) {
        listUpdateCallback.onDataSetChanged();
    }

    @Override
    public boolean shouldUseMultiThreading() {
        return false;
    }
}