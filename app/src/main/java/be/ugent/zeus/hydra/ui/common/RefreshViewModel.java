package be.ugent.zeus.hydra.ui.common;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.repository.Result;
import be.ugent.zeus.hydra.repository.data.RefreshLiveData;

/**
 * @author Niko Strijbol
 */
public abstract class RefreshViewModel<D> extends AndroidViewModel {

    private LiveData<Boolean> refreshing;
    private LiveData<Result<D>> data;

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
            data = provideData();
        }
        return data;
    }

    protected abstract LiveData<Result<D>> provideData();
}