package be.ugent.zeus.hydra.fragments.home.loader;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.OperationCanceledException;

import be.ugent.zeus.hydra.fragments.home.requests.HomeFeedRequest;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.utils.IterableSparseArray;

import java.util.List;

/**
 * A customized loader for the home feed. This loaders takes a number of {@link HomeFeedRequest}s, and starts executing
 * them. Requests are processed in the same order they were added.
 *
 * Because loading a lot of requests may result in a long waiting time for the user, this loader will report partial
 * results to a {@link HomeFeedLoaderCallback}. Note that this is only called during the actual loading. If the data
 * is cached, this is not called, as the data is returned immediately using the normal methods.
 *
 * Note: while this loader could be abstracted to support other types, we currently don't do this as this would a lot
 * of complexity for nothing.
 *
 * @author Niko Strijbol
 */
public class HomeFeedLoader extends AsyncTaskLoader<IterableSparseArray<ThrowableEither<List<HomeCard>>>> {

    //The requests to get data from.
    private IterableSparseArray<HomeFeedRequest> requests = new IterableSparseArray<>();
    //The results for each request. This is cached as a block, you cannot refresh individual results.
    private IterableSparseArray<ThrowableEither<List<HomeCard>>> results;
    //The listener. This is a copy of the built-in listener, but casted and accessible.
    private HomeFeedLoaderCallback listener;

    /**
     * @param context The context.
     */
    public HomeFeedLoader(Context context, HomeFeedLoaderCallback callback) {
        super(context);
        this.listener = callback;
    }

    /**
     * Schedule a new request. You must add requests before the loader has started.
     *
     * @param request The request to add.
     */
    public void addRequest(HomeFeedRequest request) {

        if(isStarted()) {
            throw new IllegalStateException("Requests must be added before the loader is started.");
        }

        requests.append(request.getCardType(), request);
    }

    /**
     * {@inheritDoc}
     *
     * This method will call the {@link HomeFeedLoaderCallback}'s methods.
     */
    @Override
    public IterableSparseArray<ThrowableEither<List<HomeCard>>> loadInBackground() {

        //Handler to post updates to the UI thread.
        Handler handler = new Handler(getContext().getMainLooper());

        results = new IterableSparseArray<>();

        for(final HomeFeedRequest request: requests) {
            //If the request is cancelled.
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }

            try {
                final List<HomeCard> result = request.performRequest();
                results.append(request.getCardType(), new ThrowableEither<>(result));
                if(listener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPartialResult(result, request.getCardType());
                        }
                    });
                }
            } catch (RequestFailureException e) {
                results.append(request.getCardType(), new ThrowableEither<List<HomeCard>>(e));
                if(listener != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onPartialError(request.getCardType());
                        }
                    });
                }
            }
        }

        return results;
    }

    @Override
    public void deliverResult(IterableSparseArray<ThrowableEither<List<HomeCard>>> data) {

        // The Loader has been reset; ignore the result and invalidate the data.
        if (isReset()) {
            return;
        }

        // Set the data in the loader.
        this.results = data;

        // If the Loader is in a started state, deliver the results to the client.
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        // If the data is available, deliver it.
        if (results != null) {
            deliverResult(results);
        }

        // When the observer detects a change, it should call onContentChanged() on the Loader, which will
        // cause the next call to takeContentChanged() to return true. If this is ever the case
        // (or if the current data is null), we force a new load.
        if (takeContentChanged() || results == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();

        // Stop the request.
        cancelLoad();
    }

    @Override
    public void unregisterListener(OnLoadCompleteListener<IterableSparseArray<ThrowableEither<List<HomeCard>>>> listener) {
        super.unregisterListener(listener);
        this.listener = null;
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader has stopped.
        onStopLoading();

        // Reset the data.
        results = null;
    }
}