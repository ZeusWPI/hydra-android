package be.ugent.zeus.hydra.common;

import android.content.Context;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.requests.AbstractRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

/**
 * Contains some a static method to reduce code duplication when working with requests. This needs to be like this since
 * we need this in both activities and fragments, and Java does not support multiple inheritance.
 *
 * TODO: change this to support all data types, not just lists.
 *
 * @author Niko Strijbol
 */
public class RequestHandler {

    /**
     * Perform a request with support for refresh.
     *
     * @param force Force new data, i.e. refresh.
     * @param r The request to perform.
     * @param requester The one who requests the data.
     * @param <D> The result.
     */
    public static <D, L extends ArrayList<D>> void performListRequest(final boolean force, final AbstractRequest<L> r, final Requester<ArrayList<D>> requester) {
        //We must cast, but they are always the same, but Java doesn't know that unfortunately.
        @SuppressWarnings("unchecked")
        final Requester<L> requesterL = (Requester<L>) requester;
        performRequest(force, r, requesterL);
    }

    /**
     * Perform a request with support for refresh.
     *
     * @param force Force new data, i.e. refresh.
     * @param r The request to perform.
     * @param requester The one who requests the data.
     * @param <D> The result.
     */
    public static <D> void performRequest(final boolean force, final AbstractRequest<D> r, final Requester<D> requester) {

        SpiceManager manager = requester.getManager();

        if (force) {
            manager.removeDataFromCache(r.getResultType(), r.getCacheKey());
        }

        manager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<D>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                requester.requestFailure();
            }

            @Override
            public void onRequestSuccess(final D menuList) {
                requester.receiveData(menuList);
                if (force) {
                    Toast.makeText(requester.getContext(), R.string.end_refresh, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Interface for objects that can handle requests.
     */
    public interface Requester<D> {

        /**
         * @return The manager for the request.
         */
        SpiceManager getManager();

        /**
         * Called when the requests has failed.
         */
        void requestFailure();

        /**
         * Do a request. Inside this method it is recommended to
         * use one of the other methods, e.g. {@link #performRequest(boolean, AbstractRequest, Requester)}.
         *
         * @param refresh If it is a refresh or not.
         */
        void performRequest(final boolean refresh);

        /**
         * @return The context.
         */
        Context getContext();

        /**
         * Called when the request was able to produce data.
         *
         * @param data The data.
         */
        void receiveData(D data);
    }
}
