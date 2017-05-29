package be.ugent.zeus.hydra.ui.common;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.repository.RequestRepository;
import be.ugent.zeus.hydra.repository.Result;

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
    private LiveData<Result<D>> articleData;

    public RequestViewModel(Application application) {
        super(application);
        requestRepository = new RequestRepository<>(application, true);
    }

    /**
     * @return The actual data.
     */
    @Override
    public LiveData<Result<D>> getData() {
        if (articleData == null) {
            articleData = requestRepository.load(getRequest());
        }

        return articleData;
    }

    /**
     * @return The request to use.
     */
    protected abstract Request<D> getRequest();
}