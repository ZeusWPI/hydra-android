package be.ugent.zeus.hydra.ui.common;

import android.app.Application;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.repository.requests.RequestRepository;
import be.ugent.zeus.hydra.repository.requests.Result;

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

    private final RequestRepository<D> requestRepository;

    public RequestViewModel(Application application) {
        super(application);
        requestRepository = new RequestRepository<>(application);
    }

    /**
     * @return The actual data.
     */
    @Override
    public BaseLiveData<Result<D>> constructDataInstance() {
        return requestRepository.load(getRequest());
    }

    /**
     * @return The request to use.
     */
    protected abstract Request<D> getRequest();
}