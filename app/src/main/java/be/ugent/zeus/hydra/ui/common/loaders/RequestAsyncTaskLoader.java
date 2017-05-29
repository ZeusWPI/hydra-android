package be.ugent.zeus.hydra.ui.common.loaders;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.RequestException;
import be.ugent.zeus.hydra.repository.data.ModelLiveData;
import java8.util.function.Function;


/**
 * Loader to load data from a {@link Request}.
 *
 * @author Niko Strijbol
 *
 * @deprecated Use the new architecture components approach, see {@link ModelLiveData}.
 */
@Deprecated
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
            return request.performRequest(null);
        } catch (RequestException e) {
            throw new LoaderException(e);
        }
    }

    public static <D> Function<Context, AbstractLoader<D>> fromRequest(Request<D> request) {
        return c -> new RequestAsyncTaskLoader<>(c, request);
    }
}