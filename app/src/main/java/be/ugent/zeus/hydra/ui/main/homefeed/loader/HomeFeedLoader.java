package be.ugent.zeus.hydra.ui.main.homefeed.loader;

import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.OperationCanceledException;
import android.util.Log;
import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.auth.AccountUtils;
import be.ugent.zeus.hydra.data.database.minerva.DatabaseBroadcaster;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.ui.common.loaders.BroadcastListener;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedFragment;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.main.homefeed.content.debug.WaitRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.event.EventRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.agenda.MinervaAgendaRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.minerva.announcement.MinervaAnnouncementRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.news.NewsRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.resto.RestoRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.schamper.SchamperRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.specialevent.SpecialEventRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.urgent.UrgentRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.feed.FeedOperation;
import be.ugent.zeus.hydra.ui.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import be.ugent.zeus.hydra.utils.IterableSparseArray;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import java8.util.J8Arrays;
import java8.util.function.IntPredicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.*;

import static be.ugent.zeus.hydra.ui.main.homefeed.feed.OperationFactory.add;
import static be.ugent.zeus.hydra.ui.main.homefeed.feed.OperationFactory.get;

/**
 * A customized loader for the home feed. This loaders takes a number of {@link HomeFeedRequest}s, and starts executing
 * them. Requests are processed in the same order they were added.
 *
 * Because loading a lot of operations may result in a long waiting time for the user.
 *
 * TODO: documentation
 *
 * The results are also prepared in the
 * background, so you should not have to do any processing in the CallBack.
 *
 * Note: while this loader could be abstracted to support other types, we currently don't do this as this would a lot
 * of complexity for nothing.
 *
 * @author Niko Strijbol
 */
public class HomeFeedLoader extends AsyncTaskLoader<LoaderResult> {

    private static final String TAG = "HomeFeedLoader";
    private static final boolean ADD_STALL_REQUEST = false;

    //The data
    private LoaderResult data;

    //For which settings the loader must refresh
    private static String[] watchedPreferences = {
            HomeFeedFragment.PREF_DISABLED_CARDS,
            AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING,
            RestoPreferenceFragment.PREF_RESTO
    };

    private PreferenceListener preferenceListener;
    private BroadcastListener broadcastReceiver;
    private boolean refreshOnce;

    /**
     * @param context The context.
     */
    public HomeFeedLoader(Context context) {
        super(context);
    }

    /**
     * Tell the loader that it's data must be refreshed, not just reloaded.
     */
    public void flagForRefresh() {
        this.refreshOnce = true;
    }

    @Override
    public LoaderResult loadInBackground() {

        Handler handler = new Handler(Looper.getMainLooper());

        // Get the operations.
        IterableSparseArray<FeedOperation> operations = scheduleOperations();

        //We initialize with a copy of the existing data; we do reset the errors.
        List<HomeCard> results;

        if (data == null) {
            results = Collections.emptyList();
        } else {
            results = data.getData();
        }

        Set<Integer> errors = new HashSet<>();

        for (final FeedOperation operation: operations) {
            //If the request is cancelled.
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }

            results = executeOperation(operation, errors, results);

            List<HomeCard> finalResults = new ArrayList<>(results);
            // Deliver intermediary results.
            Log.d(TAG, "loadInBackground: Operation " + operation + " completed.");
            handler.post(() -> deliverResult(new LoaderResult(finalResults, errors, false)));
        }

        return new LoaderResult(results, errors, true);
    }

    private List<HomeCard> executeOperation(FeedOperation operation, Set<Integer> errors, List<HomeCard> results) {
        try {
            return operation.transform(results);
        } catch (RequestFailureException e) {
            errors.add(operation.getCardType());
            return results;
        }
    }

    @Override
    public void deliverResult(LoaderResult data) {

        // The Loader has been reset; ignore the result and invalidate the data.
        if (isReset()) {
            return;
        }

        if (data.isCompleted()) {
            // Set the data in the loader.
            this.data = data;
        }

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
            deliverResult(data);
        }

        // When the observer detects a change, it should call onContentChanged() on the Loader, which will
        // cause the next call to takeContentChanged() to return true. If this is ever the case
        // (or if the current data is null), we force a new load.
        if (takeContentChanged() || data == null || !data.isCompleted()) {
            forceLoad();
        }

        // Listen to changes
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferenceListener = new PreferenceListener();
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SyncBroadcast.SYNC_DONE);
        intentFilter.addAction(DatabaseBroadcaster.MINERVA_ANNOUNCEMENT_UPDATED);
        broadcastReceiver = new BroadcastListener(this);
        manager.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();

        // Stop the request.
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();

        Log.d(TAG, "onReset: Reset loader");

        // Ensure the loader has stopped. This should not be necessary, but all Google's examples do it.
        // However, we only do it when the loader is started.
        if (isStarted()) {
            onStopLoading();
        }

        // Stop listening
        if (preferenceListener != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener);
            preferenceListener = null;
        }
        if (broadcastReceiver != null) {
            LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getContext());
            manager.unregisterReceiver(broadcastReceiver);
            Log.i(TAG, "onReset: UNREGISTERED RECEIVER, instance: " + broadcastReceiver);
            broadcastReceiver = null;
        }

        // Reset the data.
        data = null;
    }

    private class PreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (J8Arrays.stream(watchedPreferences).anyMatch(key::contains)) {
                Log.d(TAG, "onSharedPreferenceChanged: Content was changed due to SharedPreferences.");
                onContentChanged();
            }
        }
    }


    /**
     * Called by the loader to retrieve the operations that should be executed. This method may be called from another
     * thread.
     *
     * @return The operations to execute.
     */
    private IterableSparseArray<FeedOperation> scheduleOperations() {

        FeedCollection operations = new FeedCollection();
        Context c = getContext();

        Set<Integer> s = StreamSupport.stream(android.support.v7.preference.PreferenceManager
                .getDefaultSharedPreferences(c)
                .getStringSet(HomeFeedFragment.PREF_DISABLED_CARDS, Collections.emptySet()))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        // Don't do Minerva if there is no account.
        if (!AccountUtils.hasAccount(c)) {
            s.add(HomeCard.CardType.MINERVA_AGENDA);
            s.add(HomeCard.CardType.MINERVA_ANNOUNCEMENT);
        }

        // Don't do Urgent.fm if there is no network.
        if (!NetworkUtils.isConnected(c)) {
            s.add(HomeCard.CardType.URGENT_FM);
        }

        // Test if the card type is ignored or not.
        IntPredicate d = s::contains;

        //Always insert the special events.
        operations.add(add(new SpecialEventRequest(c, refreshOnce)));

        //Add other stuff if needed
        operations.add(get(d, () -> new RestoRequest(c, refreshOnce), HomeCard.CardType.RESTO));
        operations.add(get(d, () -> new EventRequest(c, refreshOnce), HomeCard.CardType.ACTIVITY));
        operations.add(get(d, () -> new SchamperRequest(c, refreshOnce), HomeCard.CardType.SCHAMPER));
        operations.add(get(d, () -> new NewsRequest(c, refreshOnce), HomeCard.CardType.NEWS_ITEM));
        operations.add(get(d, () -> new MinervaAnnouncementRequest(c), HomeCard.CardType.MINERVA_ANNOUNCEMENT));
        operations.add(get(d, () -> new MinervaAgendaRequest(c), HomeCard.CardType.MINERVA_AGENDA));
        operations.add(get(d, UrgentRequest::new, HomeCard.CardType.URGENT_FM));

        // Add debug request.
        if (BuildConfig.DEBUG && ADD_STALL_REQUEST) {
            operations.add(add(new WaitRequest()));
        }

        refreshOnce = false;

        return operations;
    }
}