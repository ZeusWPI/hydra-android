package be.ugent.zeus.hydra.fragments.home.loader;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.util.DiffUtil;
import android.util.Log;
import android.util.Pair;
import be.ugent.zeus.hydra.fragments.home.requests.HomeFeedRequest;
import be.ugent.zeus.hydra.models.association.Association;
import be.ugent.zeus.hydra.models.cards.EventCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.utils.IterableSparseArray;

import java.util.*;

import static be.ugent.zeus.hydra.models.cards.HomeCard.CardType.ACTIVITY;

/**
 * A customized loader for the home feed. This loaders takes a number of {@link HomeFeedRequest}s, and starts executing
 * them. Requests are processed in the same order they were added.
 *
 * Because loading a lot of requests may result in a long waiting time for the user, this loader will report partial
 * results to a {@link HomeFeedLoaderCallback}, and {@link DataCallback}. The results are also prepared in the background,
 * so you should not have to do any processing in the CallBack.
 *
 * When the data is cached, the {@link DataCallback} is still called once with the data, but the {@link HomeFeedLoaderCallback}
 * is not called.
 *
 * Note: while this loader could be abstracted to support other types, we currently don't do this as this would a lot
 * of complexity for nothing.
 *
 * @author Niko Strijbol
 */
public class HomeFeedLoader extends AsyncTaskLoader<Pair<Set<Integer>, List<HomeCard>>> {

    //The requests to get data from.
    private IterableSparseArray<HomeFeedRequest> requests = new IterableSparseArray<>();
    //The listener. This is a copy of the built-in listener, but casted and accessible.
    private HomeFeedLoaderCallback listener;
    //The data provider
    private DataCallback<List<HomeCard>> dataCallback;

    //The data
    private Pair<Set<Integer>, List<HomeCard>> data;

    /**
     * @param context The context.
     */
    public HomeFeedLoader(Context context, HomeFeedLoaderCallback callback, DataCallback<List<HomeCard>> dataCallback) {
        super(context);
        this.listener = callback;
        this.dataCallback = dataCallback;
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
    public Pair<Set<Integer>, List<HomeCard>> loadInBackground() {

        //Handler to post updates to the UI thread.
        Handler handler = new Handler(getContext().getMainLooper());

        //We initialize with a copy of the existing data; we do reset the errors.
        List<HomeCard> results;
        if(dataCallback != null) {
            results = new ArrayList<>(dataCallback.getCurrentList());
        } else {
            results = new ArrayList<>();
        }
        Set<Integer> errors = new HashSet<>();

        for(final HomeFeedRequest request: requests) {
            //If the request is cancelled.
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }

            try {
                final List<HomeCard> result = request.performRequest();
                addAndSort(results, result, request.getCardType());

                if(listener != null && dataCallback != null) {
                    //Get a copy of the data
                    final List<HomeCard> newData = new ArrayList<>(results);
                    final List<HomeCard> oldData = dataCallback.getCurrentList();
                    final DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new HomeDiffCallback(oldData, newData), false);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //The listener may be set to null at this point.
                            if(listener != null && dataCallback != null) {
                                dataCallback.onDataUpdated(newData, diff);
                                listener.onNewDataUpdate(request.getCardType());
                            }
                        }
                    });
                }
            } catch (RequestFailureException e) {
                errors.add(request.getCardType());
                handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(listener != null) {
                                listener.onPartialError(request.getCardType());
                            }
                        }
                    });
            }
        }

        return new Pair<>(errors, results);
    }

    private void addAndSort(List<HomeCard> list, Collection<HomeCard> toAdd, @HomeCard.CardType int type) {
        //Remove all cards from this type
        Iterator<HomeCard> it = list.iterator();
        while (it.hasNext()) { // Why no filter :(
            HomeCard c = it.next();
            if (c.getCardType() == type) {
                it.remove();
            }
        }

        list.addAll(toAdd);
        Collections.sort(list);
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
    public void unregisterListener(OnLoadCompleteListener<Pair<Set<Integer>, List<HomeCard>>> listener) {
        super.unregisterListener(listener);
        this.listener = null;
        this.dataCallback = null;
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader has stopped.
        onStopLoading();

        // Reset the data.
        data = null;
    }

    private void removeCards(List<HomeCard> list, @HomeCard.CardType int type) {
        //Remove all cards from this type
        Iterator<HomeCard> it = list.iterator();
        while (it.hasNext()) { // Why no filter :(
            HomeCard c = it.next();
            if (c.getCardType() == type) {
                it.remove();
            }
        }
    }

    public void removeCards(@HomeCard.CardType int type) {
        if(data != null) {
            removeCards(data.second, type);
        }
    }

    public void removeAssociations(Association association) {
        if(data == null) {
            return;
        }
        //Why no filter :(
        Iterator<HomeCard> it = data.second.iterator();
        while (it.hasNext()) { // Why no filter :(
            HomeCard c = it.next();
            if (c.getCardType() == ACTIVITY) {
                EventCard card = c.checkCard(ACTIVITY);
                if (card.getEvent().getAssociation().getInternalName().equals(association.getInternalName())) {
                    it.remove();
                }
            }
        }
    }
}