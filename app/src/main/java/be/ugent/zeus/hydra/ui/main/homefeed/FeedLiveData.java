package be.ugent.zeus.hydra.ui.main.homefeed;

import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.data.auth.AccountUtils;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.repository.requests.Result;
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
import be.ugent.zeus.hydra.ui.main.homefeed.operations.FeedOperation;
import be.ugent.zeus.hydra.ui.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.ui.preferences.RestoPreferenceFragment;
import be.ugent.zeus.hydra.utils.IterableSparseArray;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import java8.util.J8Arrays;
import java8.util.function.IntPredicate;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.*;

import static be.ugent.zeus.hydra.ui.main.homefeed.operations.OperationFactory.add;
import static be.ugent.zeus.hydra.ui.main.homefeed.operations.OperationFactory.get;

/**
 * The data source for the home feed. The home feed is a feed that mixes data from different sources. Additions or
 * deletions from the feed are represented by {@link FeedOperation}s.
 *
 * This class supports two refresh modes: full and partial. The full method is the default and will refresh all
 * feed sources. This is the default method.
 *
 * By passing the correct parameter, you can specify to only update the source for a specific card type. For this,
 * the feed data supports the constant {@link #REFRESH_HOMECARD_TYPE}.
 *
 * You must pass this in a bundle to {@link #flagForRefresh(Bundle)}.
 *
 * @author Niko Strijbol
 */
public class FeedLiveData extends BaseLiveData<Result<List<HomeCard>>> {

    /**
     * Sets which card type should be updated. The default value is {@link #REFRESH_ALL_CARDS}.
     */
    public static final String REFRESH_HOMECARD_TYPE = "be.ugent.zeus.hydra.data.refresh.homecard.type";
    public static final int REFRESH_ALL_CARDS = -20;

    private static final String TAG = "HomeFeedLoader";
    private RestoListener restoListener = new RestoListener();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (SyncBroadcast.SYNC_DONE.equals(intent.getAction())) {
                Bundle extras = intent.getExtras() == null ? Bundle.EMPTY : intent.getExtras();
                flagForRefresh(extras);
            }
        }
    };

    private final Context applicationContext;

    //For which settings the loader must refresh
    private static String[] watchedPreferences = {
            HomeFeedFragment.PREF_DISABLED_CARDS,
            AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING,
            RestoPreferenceFragment.PREF_RESTO,
            HomeFeedFragment.PREF_DISABLED_SPECIALS
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
        preferences.registerOnSharedPreferenceChangeListener(restoListener);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(applicationContext);
        manager.registerReceiver(broadcastReceiver, new IntentFilter(SyncBroadcast.SYNC_DONE));
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
            flagForRefresh();
        }
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        preferences.unregisterOnSharedPreferenceChangeListener(restoListener);
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(applicationContext);
        manager.unregisterReceiver(broadcastReceiver);
    }

    private class RestoListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // If it is a value we are looking for, save the value.
            // We don't need to update for these values anymore, since we already do this manually.
            if (J8Arrays.stream(watchedPreferences).anyMatch(key::contains)) {
                oldPreferences.put(key, sharedPreferences.getAll().get(key));
            }
            if (RestoPreferenceFragment.PREF_RESTO.equals(key)) {
                Bundle ex = new Bundle();
                ex.putInt(REFRESH_HOMECARD_TYPE, HomeCard.CardType.RESTO);
                flagForRefresh(ex);
            }
        }
    }

    private List<HomeCard> executeOperation(@Nullable Bundle args, FeedOperation operation, Set<Integer> errors, List<HomeCard> results) {

        Result<List<HomeCard>> result = operation.transform(args, results);

        if (result.hasException()) {
            errors.add(operation.getCardType());
        }

        return result.orElse(results);
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
                Iterable<FeedOperation> operations = findOperations(scheduleOperations(), bundle);

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
                    if (isCancelled()) {
                        return null;
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
        if (BuildConfig.DEBUG && BuildConfig.DEBUG_HOME_STREAM_STALL) {
            operations.add(add(new WaitRequest()));
        }

        return operations;
    }

    /**
     * Filter the requests to only include the requests that should be executed.
     *
     * @param allOperations A list of all possible operations.
     * @param args The arguments to determine which requests will be executed.
     * @return The requests to be executed.
     */
    private Iterable<FeedOperation> findOperations(IterableSparseArray<FeedOperation> allOperations, @Nullable Bundle args) {

        // If there are no arguments, or we must do all operations, do nothing.
        if (args == null || args.getInt(REFRESH_HOMECARD_TYPE, REFRESH_ALL_CARDS) == REFRESH_ALL_CARDS) {
            Log.i(TAG, "Returning all card types.");
            return allOperations;
        }

        int cardType = args.getInt(REFRESH_HOMECARD_TYPE, -50);
        FeedOperation operation = allOperations.get(cardType);
        if (operation == null) {
            // Something went wrong.
            Log.w(TAG, "Invalid card type " + cardType + " was passed. Defaulting to all types.");
            return allOperations;
        }
        Log.i(TAG, "Returning card type " + cardType);
        return Collections.singleton(operation);
    }
}