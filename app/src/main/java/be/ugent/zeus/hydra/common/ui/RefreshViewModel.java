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

package be.ugent.zeus.hydra.common.ui;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import androidx.lifecycle.*;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * @author Niko Strijbol
 */
public abstract class RefreshViewModel<D> extends AndroidViewModel implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "RefreshViewModel";

    private LiveData<Boolean> refreshing;
    private BaseLiveData<Result<D>> data;

    public RefreshViewModel(Application application) {
        super(application);
    }

    /**
     * @return The refreshing status.
     */
    public LiveData<Boolean> getRefreshing() {
        if (refreshing == null) {
            refreshing = buildRefreshLiveData();
        }

        return refreshing;
    }

    /**
     * Internal get that exposes more implementation details than {@link #getData()}.
     *
     * @return The live data.
     */
    private BaseLiveData<Result<D>> internalGet() {
        if (data == null) {
            data = constructDataInstance();
        }
        return data;
    }

    /**
     * @return The actual data.
     */
    public LiveData<Result<D>> getData() {
        return internalGet();
    }

    /**
     * Provide (and construct) a fresh instance of the data. The parent class manages access. Other classes should
     * obtain the data by calling {@link #getData()}.
     *
     * @return The data.
     */
    protected abstract BaseLiveData<Result<D>> constructDataInstance();

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "Destroyed the view model.");
        refreshing = null;
        data = null;
    }

    public void requestRefresh() {
        if (data != null) {
            data.flagForRefresh();
        }
    }

    public void requestRefresh(Bundle args) {
        if (data != null) {
            data.flagForRefresh(args);
        }
    }

    /**
     * Forwards the call to {@link #requestRefresh()}.
     */
    @Override
    public void onRefresh() {
        requestRefresh();
    }

    /**
     * Construct the refresh live data.
     *
     * @return The refresh live data.
     */
    private LiveData<Boolean> buildRefreshLiveData() {
        MediatorLiveData<Boolean> result = new MediatorLiveData<>();
        MutableLiveData<Boolean> refreshLiveData = new MutableLiveData<>();
        result.addSource(internalGet(), d -> result.setValue(d != null && !d.isDone()));
        result.addSource(refreshLiveData, result::setValue);
        internalGet().registerRefreshListener(() -> result.setValue(true));
        return result;
    }
}