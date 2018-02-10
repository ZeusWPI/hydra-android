package be.ugent.zeus.hydra.common.ui;

import android.app.Application;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * Generic view model with boiler plate for using a {@link Request} as data.
 *
 * It also supports a refresh status.
 *
 * @param <D> The type of the data.
 *
 * @author Niko Strijbol
 */
public abstract class RequestViewModel<D> extends RefreshViewModel<D> {

    public RequestViewModel(Application application) {
        super(application);
    }

    /**
     * @return The actual data.
     */
    @Override
    public BaseLiveData<Result<D>> constructDataInstance() {
        return new RequestLiveData<>(getApplication(), getRequest());
    }

    /**
     * @return The request to use.
     */
    protected abstract Request<D> getRequest();
}