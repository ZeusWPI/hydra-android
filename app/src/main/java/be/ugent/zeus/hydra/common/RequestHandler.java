package be.ugent.zeus.hydra.common;

import android.content.Context;
import android.os.Parcelable;
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
     * @param <L> Necessary due to type erasure. List of {@code <D>}'s.
     */
    public static <D extends Parcelable, L extends ArrayList<D>> void performRequest(final boolean force, final AbstractRequest<L> r, final Requester<D> requester) {

        SpiceManager manager = requester.getManager();

        if (force) {
            manager.removeDataFromCache(r.getResultType(), r.getCacheKey());
        }

        manager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<L>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                requester.requestFailure();
            }

            @Override
            public void onRequestSuccess(final L menuList) {
                if (menuList.isEmpty()) {
                    requester.requestFailure();
                } else {
                    requester.receiveData(menuList);
                    if (force) {
                        Toast.makeText(requester.getContext(), R.string.end_refresh, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    /**
     * Interface for objects that can handle requests.
     *
     * @param <D> The result.
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
         * use {@link #performRequest(boolean, AbstractRequest, Requester)}.
         *
         * @param refresh If it is a refresh or not.
         */
        void performRequest(final boolean refresh);

        /**
         * Called when the request was able to produce data.
         *
         * @param data The data.
         */
        void receiveData(ArrayList<D> data);

        /**
         * @return The context.
         */
        Context getContext();
    }
}
