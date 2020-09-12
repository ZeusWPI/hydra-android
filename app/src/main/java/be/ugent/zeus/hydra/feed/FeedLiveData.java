package be.ugent.zeus.hydra.feed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.association.AssociationStore;
import be.ugent.zeus.hydra.common.ExtendedSparseArray;
import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.debug.WaitRequest;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;
import be.ugent.zeus.hydra.feed.cards.event.EventRequest;
import be.ugent.zeus.hydra.feed.cards.library.LibraryRequest;
import be.ugent.zeus.hydra.feed.cards.news.NewsRequest;
import be.ugent.zeus.hydra.feed.cards.resto.RestoRequest;
import be.ugent.zeus.hydra.feed.cards.schamper.SchamperRequest;
import be.ugent.zeus.hydra.feed.cards.specialevent.LimitingSpecialEventRequest;
import be.ugent.zeus.hydra.feed.cards.urgent.UrgentRequest;
import be.ugent.zeus.hydra.feed.operations.FeedOperation;
import be.ugent.zeus.hydra.resto.RestoPreferenceFragment;

import static be.ugent.zeus.hydra.feed.operations.OperationFactory.add;
import static be.ugent.zeus.hydra.feed.operations.OperationFactory.get;

/**
 * The data source for the home feed. The home feed is a feed that mixes data from different sources. Additions or
 * deletions from the feed are represented by {@link FeedOperation}s.
 * <p>
 * This class supports two refresh modes: full and partial. The full method is the default and will refresh all
 * feed sources. This is the default method.
 * <p>
 * By passing the correct parameter, you can specify to only update the source for a specific card type. For this,
 * the feed data supports the constant {@link #REFRESH_HOMECARD_TYPE}.
 * <p>
 * You must pass this in a bundle to {@link #flagForRefresh(Bundle)}.
 *
 * @author Niko Strijbol
 */
public class FeedLiveData extends BaseLiveData<Result<List<Card>>> {

    /**
     * Sets which card type should be updated. The default value is {@link #REFRESH_ALL_CARDS}.
     */
    static final String REFRESH_HOMECARD_TYPE = "be.ugent.zeus.hydra.data.refresh.homecard.type";
    private static final int REFRESH_ALL_CARDS = -20;

    private static final String TAG = "HomeFeedLoader";
    // For which settings the loader must refresh.
    private static final String[] watchedPreferences = {
            HomeFeedFragment.PREF_DISABLED_CARD_TYPES,
            AssociationStore.PREF_WHITELIST,
            RestoPreferenceFragment.PREF_RESTO_KEY,
            RestoPreferenceFragment.PREF_RESTO_NAME,
            HomeFeedFragment.PREF_DISABLED_CARD_HACK
    };
    private final SharedPreferences.OnSharedPreferenceChangeListener restoListener = new RestoListener();
    private final Context applicationContext;
    private final Map<String, Object> oldPreferences = new HashMap<>();

    FeedLiveData(Context context) {
        this.applicationContext = context.getApplicationContext();
        loadData();
    }

    private static List<Card> executeOperation(@Nullable Bundle args,
                                               FeedOperation operation,
                                               Collection<Integer> errors,
                                               List<Card> results) {

        Result<List<Card>> result = operation.transform(args, results);

        if (result.hasException()) {
            errors.add(operation.getCardType());
        }

        return result.orElse(results);
    }

    /**
     * Filter the requests to only include the requests that should be executed.
     *
     * @param allOperations A list of all possible operations.
     * @param args          The arguments to determine which requests will be executed.
     * @return The requests to be executed.
     */
    private static Iterable<FeedOperation> findOperations(ExtendedSparseArray<FeedOperation> allOperations, @NonNull Bundle args) {

        // If there are no arguments, or we must do all operations, do nothing.
        if (args.getInt(REFRESH_HOMECARD_TYPE, REFRESH_ALL_CARDS) == REFRESH_ALL_CARDS) {
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

    @Override
    protected void onActive() {
        super.onActive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        preferences.registerOnSharedPreferenceChangeListener(restoListener);
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
    }

    /**
     * Load the actual data.
     *
     * @param bundle The arguments for the request.
     */
    @Override
    @SuppressLint("StaticFieldLeak")
    protected void loadData(@NonNull Bundle bundle) {
        new AsyncTask<Void, Result<List<Card>>, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                // Get the operations.
                Log.d(TAG, "doInBackground: received load request with " + bundle);
                Iterable<FeedOperation> operations = findOperations(scheduleOperations(), bundle);

                // Get existing value if needed.
                Result<List<Card>> loaderResult = getValue();
                //We initialize with a copy of the existing data; we do reset the errors.
                List<Card> results;

                if (loaderResult == null) {
                    results = Collections.emptyList();
                } else {
                    results = loaderResult.orElse(new ArrayList<>());
                }

                Set<Integer> errors = new HashSet<>();
                Result<List<Card>> result = null;

                for (FeedOperation operation : operations) {
                    if (isCancelled()) {
                        return null;
                    }

                    results = executeOperation(bundle, operation, errors, results);

                    List<Card> finalResults = new ArrayList<>(results);
                    // Deliver intermediary results.
                    Log.d(TAG, "loadInBackground: Operation " + operation + " completed.");
                    Result.Builder<List<Card>> builder = new Result.Builder<List<Card>>()
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
            protected final void onProgressUpdate(Result<List<Card>>... values) {
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
    private ExtendedSparseArray<FeedOperation> scheduleOperations() {

        FeedCollection operations = new FeedCollection();
        Context c = applicationContext;
        Set<Integer> disabled = PreferenceManager.getDefaultSharedPreferences(c)
                .getStringSet(HomeFeedFragment.PREF_DISABLED_CARD_TYPES, Collections.emptySet())
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toSet());

        // Don't do Urgent.fm if there is no network.
        if (!NetworkUtils.isConnected(c)) {
            disabled.add(Card.Type.URGENT_FM);
        }

        // Test if the card type is ignored or not.
        IntPredicate d = disabled::contains;

        // Repositories
        DismissalDao cd = Database.get(c).getCardDao();

        // Always insert the special events.
        operations.add(add(new LimitingSpecialEventRequest(c, cd)));

        // Add other stuff if needed.
        operations.add(get(d, () -> new RestoRequest(c, cd), Card.Type.RESTO));
        operations.add(get(d, () -> new EventRequest(c, cd), Card.Type.ACTIVITY));
        operations.add(get(d, () -> new SchamperRequest(c, cd), Card.Type.SCHAMPER));
        operations.add(get(d, () -> new NewsRequest(c, cd), Card.Type.NEWS_ITEM));
        operations.add(get(d, UrgentRequest::new, Card.Type.URGENT_FM));
        operations.add(get(d, () -> new LibraryRequest(c), Card.Type.LIBRARY));

        // Add debug request.
        if (BuildConfig.DEBUG && BuildConfig.DEBUG_HOME_STREAM_STALL) {
            operations.add(add(new WaitRequest()));
        }

        return operations;
    }

    private class RestoListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // If it is a value we are looking for, save the value.
            // We don't need to update for these values anymore, since we already do this manually.
            if (Arrays.stream(watchedPreferences).anyMatch(key::contains)) {
                oldPreferences.put(key, sharedPreferences.getAll().get(key));
            }
            if (RestoPreferenceFragment.PREF_RESTO_KEY.equals(key) || RestoPreferenceFragment.PREF_RESTO_NAME.equals(key)) {
                Bundle ex = new Bundle();
                ex.putInt(REFRESH_HOMECARD_TYPE, Card.Type.RESTO);
                flagForRefresh(ex);
            }
        }
    }
}
