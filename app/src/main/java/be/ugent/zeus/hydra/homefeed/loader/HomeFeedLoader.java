package be.ugent.zeus.hydra.homefeed.loader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.util.Pair;
import be.ugent.zeus.hydra.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.homefeed.content.event.EventCard;
import be.ugent.zeus.hydra.homefeed.feed.FeedOperation;
import be.ugent.zeus.hydra.homefeed.feed.OperationFactory;
import be.ugent.zeus.hydra.models.association.Association;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.utils.IterableSparseArray;
import java8.util.function.Predicate;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static be.ugent.zeus.hydra.homefeed.content.HomeCard.CardType.ACTIVITY;

/**
 * A customized loader for the home feed. This loaders takes a number of {@link HomeFeedRequest}s, and starts executing
 * them. Requests are processed in the same order they were added.
 *
 * Because loading a lot of operations may result in a long waiting time for the user, this loader will report partial
 * results to a {@link HomeFeedLoaderCallback}. The results are also prepared in the
 * background, so you should not have to do any processing in the CallBack.
 *
 * Note: while this loader could be abstracted to support other types, we currently don't do this as this would a lot
 * of complexity for nothing.
 *
 * @author Niko Strijbol
 */
public class HomeFeedLoader extends AsyncTaskLoader<Pair<Set<Integer>, List<HomeCard>>> {

    private static final String TAG = "HomeFeedLoader";

    private IterableSparseArray<FeedOperation> operations = new IterableSparseArray<>();
    //The listener. This is a copy of the built-in listener, but casted and accessible.
    private HomeFeedLoaderCallback listener;

    //The data
    private Pair<Set<Integer>, List<HomeCard>> data;

    /**
     * @param context The context.
     */
    public HomeFeedLoader(Context context, HomeFeedLoaderCallback callback) {
        super(context);
        this.listener = callback;
        Log.d(TAG, "Loader made.");
    }

    /**
     * Schedule a new operation. You must add operations before the loader has started. The new operation is append
     * to the existing operations and will be executed in order of arrival.
     *
     * @param operation The request to add.
     */
    public void addOperation(FeedOperation operation) {

        if(isStarted()) {
            throw new IllegalStateException("Operations must be added before the loader is started.");
        }

        Log.d(TAG, "addOperation: Operation added");

        operations.append(operation.getCardType(), operation);
    }

    /**
     * {@inheritDoc}
     *
     * This method will call the {@link HomeFeedLoaderCallback}'s methods.
     */
    @Override
    public Pair<Set<Integer>, List<HomeCard>> loadInBackground() {

        //Handler to post updates to the UI thread.
        Handler handler = new Handler(Looper.getMainLooper());

        //We initialize with a copy of the existing data; we do reset the errors.
        List<HomeCard> results;
        if(listener == null) {
            results = Collections.emptyList();
        } else {
            results = listener.getExistingData();
        }

        Set<Integer> errors = new HashSet<>();

        for (final FeedOperation operation: operations) {
            //If the request is cancelled.
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }

            Log.d(TAG, "Operation loaded");

            results = executeOperation(handler, operation, errors, results);
        }

        return new Pair<>(errors, results);
    }

    private List<HomeCard> executeOperation(Handler handler, FeedOperation operation, Set<Integer> errors, List<HomeCard> results) {

        try {
            //Try performing the operation.
            Pair<List<HomeCard>, DiffUtil.DiffResult> result = operation.transform(results);

            //Report the partial result to the main thread.
            handler.post(() -> {
                if (!isAbandoned()) {
                    listener.onPartialUpdate(result.first, result.second, operation.getCardType());
                }
            });

            return result.first;

        } catch (RequestFailureException e) {
            errors.add(operation.getCardType());
            //Report the error
            handler.post(() -> {
                if (!isAbandoned()) {
                    listener.onPartialError(operation.getCardType());
                }
            });

            return results;
        }
    }

    @Override
    public void deliverResult(Pair<Set<Integer>, List<HomeCard>> data) {

        // The Loader has been reset; ignore the result and invalidate the data.
        if (isReset()) {
            return;
        }

        // Set the data in the loader.
        this.data = data;

        // If the Loader is in a started state, deliver the results to the client.
        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        // If the data is available, deliver it.
        if (data != null) {
            Log.d("Loader", "Cached data");
            deliverResult(data);
        }

        // When the observer detects a change, it should call onContentChanged() on the Loader, which will
        // cause the next call to takeContentChanged() to return true. If this is ever the case
        // (or if the current data is null), we force a new load.
        if (takeContentChanged() || data == null) {
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
    protected void onAbandon() {
        super.onAbandon();
        this.listener = null;
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader has stopped.
        onStopLoading();

        // Reset the data.
        data = null;
    }

    /**
     * Remove a type of cards. Note: this will run on the main thread.
     *
     * @param type The type to remove.
     */
    public void removeType(@HomeCard.CardType int type) {

        if(!isStarted() || data == null) {
            return;
        }

        //Replace the operation with an empty one.
        FeedOperation op = OperationFactory.del(type);
        operations.append(type, op);

        //Re-execute the operation.
        List<HomeCard> currentData = data.second;
        Set<Integer> errors = data.first;
        Handler h = new Handler(Looper.getMainLooper());
        List<HomeCard> newData = executeOperation(h, op, errors, currentData);

        //Deliver the results.
        deliverResult(new Pair<>(errors, newData));
    }

    public void removeAssociations(Association association) {

        if(!isStarted() || data == null) {
            return;
        }

        //Remove cards from the association
        Predicate<HomeCard> predicate = c -> c.getCardType() == ACTIVITY && c.<EventCard>checkCard(ACTIVITY).getEvent().getAssociation().equals(association);

        List<HomeCard> currentData = data.second;
        Set<Integer> errors = data.first;
        Handler h = new Handler(Looper.getMainLooper());
        FeedOperation op = OperationFactory.del(ACTIVITY, predicate);
        List<HomeCard> newData = executeOperation(h, op, errors, currentData);

        deliverResult(new Pair<>(errors, newData));
    }
}