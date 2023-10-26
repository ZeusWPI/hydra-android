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
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.*;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * View model with support for refreshing.
 *
 * @author Niko Strijbol
 */
public abstract class RefreshViewModel extends AndroidViewModel implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "RefreshViewModel";

    private final Set<Integer> busyData = new HashSet<>();
    private LiveData<Boolean> refreshing;
    private final List<BaseLiveData<? extends Result<?>>> registeredData = new ArrayList<>();

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

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "Destroyed the view model.");
        refreshing = null;
        registeredData.clear();
        busyData.clear();
    }

    public void requestRefresh() {
        for (BaseLiveData<? extends Result<?>> data : registeredData) {
            data.flagForRefresh();
        }
    }

    public void requestRefresh(Bundle args) {
        for (BaseLiveData<? extends Result<?>> data : registeredData) {
            data.flagForRefresh(args);
        }
    }

    protected <T extends Result<?>> BaseLiveData<T> registerSource(BaseLiveData<T> data) {
        registeredData.add(data);
        if (refreshing != null && refreshing.hasActiveObservers()) {
            throw new IllegalStateException("You cannot add sources after observing has started.");
        }
        return data;
    }

    /**
     * Forwards the call to {@link #requestRefresh()}.
     */
    @Override
    public void onRefresh() {
        requestRefresh();
    }

    private LiveData<Boolean> buildRefreshLiveData() {
        busyData.clear();
        busyData.addAll(registeredData.stream().map(Object::hashCode).collect(Collectors.toList()));
        MediatorLiveData<Boolean> refreshing = new MediatorLiveData<>();
        refreshing.setValue(true);
        for (BaseLiveData<? extends Result<?>> data : registeredData) {
            refreshing.addSource(data, result -> {
                if (result.isDone()) {
                    busyData.remove(data.hashCode());
                } else {
                    busyData.add(data.hashCode());
                }
                refreshing.setValue(!busyData.isEmpty());
            });
        }
        return refreshing;
    }
}