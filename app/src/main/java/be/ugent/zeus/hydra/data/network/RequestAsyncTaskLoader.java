package be.ugent.zeus.hydra.data.network;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loaders.AbstractLoader;
import be.ugent.zeus.hydra.loaders.LoaderException;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

/**
 * Loader to load data from a {@link Request}.
 *
 * @author Niko Strijbol
 */
public class RequestAsyncTaskLoader<D> extends AbstractLoader<D> {

    private final Request<D> request;

    /**
     * @param context The context.
     * @param request The request to get the data from.
     */
    public RequestAsyncTaskLoader(Context context, Request<D> request) {
        super(context);
        this.request = request;
    }

    @NonNull
    @Override
    protected D loadData() throws LoaderException {
        try {
            return request.performRequest();
        } catch (RequestFailureException e) {
            throw new LoaderException(e);
        }
    }
}