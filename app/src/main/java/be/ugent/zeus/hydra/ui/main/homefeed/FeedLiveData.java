package be.ugent.zeus.hydra.ui.main.homefeed;

import android.arch.lifecycle.LiveData;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.OperationCanceledException;
import android.util.Log;
import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.auth.AccountUtils;
import be.ugent.zeus.hydra.data.database.minerva.DatabaseBroadcaster;
import be.ugent.zeus.hydra.data.network.exceptions.RequestException;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.data.network.requests.Result;
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
 * @author Niko Strijbol
 */
public class FeedLiveData extends LiveData<Result<List<HomeCard>>> {

    private static final String TAG = "HomeFeedLoader";
    private static final boolean ADD_STALL_REQUEST = false;
    private PreferenceListener preferenceListener = new PreferenceListener();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loadData(intent.getExtras());
        }
    };

    private final Context applicationContext;

    //For which settings the loader must refresh
    private static String[] watchedPreferences = {
            HomeFeedFragment.PREF_DISABLED_CARDS,
            AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING,
            RestoPreferenceFragment.PREF_RESTO
    };

    private Map<String, Object> oldPreferences = new HashMap<>();

    public FeedLiveData(Context context) {
        this.applicationContext = context.getApplicationContext();
        loadData(Bundle.EMPTY);
    }

    @Override
    protected void onActive() {
        super.onActive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(applicationContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SyncBroadcast.SYNC_DONE);
        intentFilter.addAction(DatabaseBroadcaster.MINERVA_ANNOUNCEMENT_UPDATED);
        intentFilter.addAction(RefreshBroadcast.BROADCAST);
        manager.registerReceiver(broadcastReceiver, intentFilter);
        Map<String, ?> prefs = preferences.getAll();
        boolean shouldRefresh = false;
        for (String preference : watchedPreferences) {
            Object newPreference = prefs.get(preference);
            if (newPreference != null) {
                if (oldPreferences.containsKey(preference) && !oldPreferences.get(preference).equals(newPreference)) {
                    shouldRefresh = true;
                }
                oldPreferences.put(preference, newPreference);
            }
        }
        if (shouldRefresh) {
            loadData(Bundle.EMPTY);
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(applicationContext);
        manager.unregisterReceiver(broadcastReceiver);
    }

    private class PreferenceListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (J8Arrays.stream(watchedPreferences).anyMatch(key::contains)) {
                Log.d(TAG, "onSharedPreferenceChanged: Content was changed due to SharedPreferences.");
                loadData(Bundle.EMPTY);
            }
        }
    }

    private List<HomeCard> executeOperation(@Nullable Bundle args, FeedOperation operation, Set<Integer> errors, List<HomeCard> results) {
        try {
            return operation.transform(args, results);
        } catch (RequestException e) {
            errors.add(operation.getCardType());
            return results;
        }
    }

    /**
     * Load the actual data.
     *
     * @param bundle The arguments for the request.
     */
    protected void loadData(@Nullable Bundle bundle) {
        new AsyncTask<Void, Result<List<HomeCard>>, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                // Get the operations.
                IterableSparseArray<FeedOperation> operations = scheduleOperations();

                // Get existing value if needed.
                Result<List<HomeCard>> loaderResult = getValue();
                //We initialize with a copy of the existing data; we do reset the errors.
                List<HomeCard> results;

                if (loaderResult == null) {
                    results = Collections.emptyList();
                } else {
                    results = loaderResult.orElse(new ArrayList<>());
                }

                Set<Integer> errors = new HashSet<>();
                Result<List<HomeCard>> result = null;

                for (final FeedOperation operation: operations) {
                    //TODO: see if this is needed or not.
                    if (false) {
                        throw new OperationCanceledException();
                    }

                    results = executeOperation(bundle, operation, errors, results);

                    List<HomeCard> finalResults = new ArrayList<>(results);
                    // Deliver intermediary results.
                    Log.d(TAG, "loadInBackground: Operation " + operation + " completed.");
                    Result.Builder<List<HomeCard>> builder = new Result.Builder<List<HomeCard>>()
                            .withData(finalResults);

                    if (!errors.isEmpty()) {
                        builder.withError(new FeedException(errors));
                    }

                    result = builder.buildPartial();
                    //noinspection unchecked
                    publishProgress(result);
                }

                if (result != null) {
                    //noinspection unchecked
                    publishProgress(result.asCompleted());
                }

                return null;
            }

            @SafeVarargs
            @Override
            protected final void onProgressUpdate(Result<List<HomeCard>>... values) {
                super.onProgressUpdate(values);
                setValue(values[0]);
            }
        }.execute();
    }

    /**
     * Called by the loader to retrieve the operations that should be executed. This method may be called from another
     * thread.
     *
     * @return The operations to execute.
     */
    private IterableSparseArray<FeedOperation> scheduleOperations() {

        FeedCollection operations = new FeedCollection();
        Context c = applicationContext;
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
        operations.add(add(new SpecialEventRequest(c)));

        //Add other stuff if needed
        operations.add(get(d, () -> new RestoRequest(c), HomeCard.CardType.RESTO));
        operations.add(get(d, () -> new EventRequest(c), HomeCard.CardType.ACTIVITY));
        operations.add(get(d, () -> new SchamperRequest(c), HomeCard.CardType.SCHAMPER));
        operations.add(get(d, () -> new NewsRequest(c), HomeCard.CardType.NEWS_ITEM));
        operations.add(get(d, () -> new MinervaAnnouncementRequest(c), HomeCard.CardType.MINERVA_ANNOUNCEMENT));
        operations.add(get(d, () -> new MinervaAgendaRequest(c), HomeCard.CardType.MINERVA_AGENDA));
        operations.add(get(d, UrgentRequest::new, HomeCard.CardType.URGENT_FM));

        // Add debug request.
        if (BuildConfig.DEBUG && ADD_STALL_REQUEST) {
            operations.add(add(new WaitRequest()));
        }

        return operations;
    }
}