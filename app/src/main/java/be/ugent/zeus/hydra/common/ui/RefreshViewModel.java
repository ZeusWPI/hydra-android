package be.ugent.zeus.hydra.common.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

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