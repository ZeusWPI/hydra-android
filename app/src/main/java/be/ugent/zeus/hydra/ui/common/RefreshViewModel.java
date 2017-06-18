package be.ugent.zeus.hydra.ui.common;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.repository.data.RefreshLiveData;

/**
 * @author Niko Strijbol
 */
public abstract class RefreshViewModel<D> extends AndroidViewModel {

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
            refreshing = RefreshLiveData.build(getApplication(), getData());
        }

        return refreshing;
    }

    /**
     * @return The actual data.
     */
    public LiveData<Result<D>> getData() {
        if (data == null) {
            data = constructDataInstance();
        }
        return data;
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

    public void requestRefresh(Context context) {
        if (data != null) {
            data.flagForRefresh(context);
        }
    }

    public void requestRefresh(Context context, Bundle args) {
        if (data != null) {
            data.flagForRefresh(context, args);
        }
    }
}