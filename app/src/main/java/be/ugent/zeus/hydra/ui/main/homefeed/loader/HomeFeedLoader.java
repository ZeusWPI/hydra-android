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
import android.util.Pair;
import be.ugent.zeus.hydra.ui.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedFragment;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.main.homefeed.feed.FeedOperation;
import be.ugent.zeus.hydra.ui.common.loaders.BroadcastListener;
import be.ugent.zeus.hydra.data.database.minerva.DatabaseBroadcaster;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.utils.IterableSparseArray;
import java8.util.J8Arrays;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    //The listener. This is a copy of the built-in listener, but casted and accessible.
    private HomeFeedLoaderCallback listener;

    //The data
    private Pair<Set<Integer>, List<HomeCard>> data;

    //For which settings the loader must refresh
    private static String[] watchedPreferences = {
            HomeFeedFragment.PREF_DISABLED_CARDS,
            AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING,
            RestoPreferenceFragment.PREF_RESTO
    };

    private PreferenceListener preferenceListener;
    private BroadcastListener broadcastReceiver;

    /**
     * @param context The context.
     */
    public HomeFeedLoader(Context context, HomeFeedLoaderCallback callback) {
        super(context);
        this.listener = callback;
        Log.d(TAG, "Loader made.");
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

        // Get the operations.
        IterableSparseArray<FeedOperation> operations;
        if (listener == null) {
            operations = new IterableSparseArray<>();
        } else {
            operations = listener.onScheduleOperations(getContext());
        }

        //We initialize with a copy of the existing data; we do reset the errors.
        List<HomeCard> results;

        if (data == null) {
            results = Collections.emptyList();
        } else {
            results = data.second;
        }

        Set<Integer> errors = new HashSet<>();

        for (final FeedOperation operation: operations) {
            //If the request is cancelled.
            if (isLoadInBackgroundCanceled()) {
                throw new OperationCanceledException();
            }

            results = executeOperation(handler, operation, errors, results);
        }

        return new Pair<>(errors, results);
    }

    private List<HomeCard> executeOperation(Handler handler, FeedOperation operation, Set<Integer> errors, List<HomeCard> results) {

        try {
            //Try performing the operation.
            List<HomeCard> result = operation.transform(results);

            //Report the partial result to the main thread.
            handler.post(() -> {
                if (isStarted() && listener != null) {
                    listener.onPartialUpdate(result, operation.getCardType());
                }
            });

            return result;

        } catch (RequestFailureException e) {
            errors.add(operation.getCardType());
            //Report the error
            handler.post(() -> {
                if (isStarted() && listener != null) {
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
            deliverResult(data);
        }

        // When the observer detects a change, it should call onContentChanged() on the Loader, which will
        // cause the next call to takeContentChanged() to return true. If this is ever the case
        // (or if the current data is null), we force a new load.
        if (takeContentChanged() || data == null) {
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
    protected void onAbandon() {
        super.onAbandon();

        //In conformance with the documentation, we don't update the listener anymore, but still keep the data.
        listener = null;
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
            broadcastReceiver = null;
        }

        //Reset the listener. Do this here as well as in onAbandon, since the latter is only called sometimes.
        //See https://medium.com/@ianhlake/onabandon-is-only-called-by-loadermanager-when-a-loader-is-restarted-via-restartloader-as-per-bdc11452c60#.mox3hc7la
        listener = null;

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
}