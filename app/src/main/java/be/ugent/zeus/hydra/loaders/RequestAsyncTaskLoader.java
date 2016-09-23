package be.ugent.zeus.hydra.loaders;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;

/**
 * Loader to load data from a {@link Request}.
 *
 * @author Niko Strijbol
 */
public class RequestAsyncTaskLoader<D> extends AbstractAsyncLoader<D> {

    private final Request<D> request;

    /**
     * @param context The context.
     * @param request The request to get the data from.
     */
    public RequestAsyncTaskLoader(Request<D> request, Context context) {
        super(context);
        this.request = request;
    }

    @NonNull
    @Override
    protected D getData() throws LoaderException {
        try {
            return request.performRequest();
        } catch (RequestFailureException e) {
            throw new LoaderException(e);
        }
    }
}