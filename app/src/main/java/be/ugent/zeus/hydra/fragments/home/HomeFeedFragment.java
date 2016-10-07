package be.ugent.zeus.hydra.fragments.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.fragments.home.loader.HomeFeedLoader;
import be.ugent.zeus.hydra.fragments.home.loader.HomeFeedLoaderCallback;
import be.ugent.zeus.hydra.fragments.home.requests.*;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.minerva.auth.AccountUtils;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.utils.IterableSparseArray;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * The fragment showing the home feed.
 *
 * The user has the possibility to decide to hide certain card types. When a user disables a certain type of cards,
 * we do not retrieve the data.
 *
 * @author Niko Strijbol
 * @author silox
 */
public class HomeFeedFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener,
        HomeFeedLoaderCallback, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "HomeFeedFragment";

    public static final String PREF_DISABLED_CARDS = "pref_disabled_cards";

    private static final int LOADER = 0;

    private boolean shouldRefresh = false;
    private boolean preferencesUpdated = false;

    private SwipeRefreshLayout swipeRefreshLayout;
    private HomeCardAdapter adapter;
    private Snackbar snackbar;

    private SharedPreferences preferences;

    private boolean wasCached = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = $(view, R.id.home_cards_view);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout = $(view, R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);

        adapter = new HomeCardAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setRefreshing(true);
        getLoaderManager().initLoader(LOADER, null, this);

        //Register this class in the settings.
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(preferencesUpdated) {
            swipeRefreshLayout.setRefreshing(true);
            restartLoader();
            preferencesUpdated = false;
        }
    }

    /**
     * If the fragment goes to pause, we don't need to restart the loaders.
     */
    @Override
    public void onPause() {
        super.onPause();
        preferencesUpdated = false;
    }

    /**
     * Restart the loaders
     */
    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER, null, this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(PREF_DISABLED_CARDS) || s.equals(AssociationSelectPrefActivity.PREF_ASSOCIATIONS_SHOWING)) {
            preferencesUpdated = true;
        }
    }

    private void showErrorMessage(String message) {
        if(snackbar == null) {
            assert getView() != null;
            snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            snackbar.setText(message);
            snackbar.show();
        }
    }

    @Override
    public void onPartialResult(List<HomeCard> data, @HomeCard.CardType int cardType) {
        adapter.updateCardItems(data, cardType);
        wasCached = false;
    }

    @Override
    public void onPartialError(@HomeCard.CardType int cardType) {
        showErrorMessage("Kon sommige dingen niet ophalen.");
    }

    @Override
    public Loader<IterableSparseArray<ThrowableEither<List<HomeCard>>>> onCreateLoader(int id, Bundle args) {

        HomeFeedLoader loader = new HomeFeedLoader(getContext(), this);

        //Always add the special events.
        //The else clause is needed to remove any existing data from the loader.
        loader.addRequest(new SpecialEventRequest(getContext(), shouldRefresh));

        if(isTypeActive(HomeCard.CardType.RESTO)) {
            loader.addRequest(new RestoRequest(getContext(), shouldRefresh));
        } else {
            onPartialResult(Collections.<HomeCard>emptyList(), HomeCard.CardType.RESTO);
        }
        if(isTypeActive(HomeCard.CardType.ACTIVITY)) {
            loader.addRequest(new EventRequest(getContext(), shouldRefresh));
        } else {
            onPartialResult(Collections.<HomeCard>emptyList(), HomeCard.CardType.ACTIVITY);
        }
        if(isTypeActive(HomeCard.CardType.SCHAMPER)) {
            loader.addRequest(new SchamperRequest(getContext(), shouldRefresh));
        } else {
            onPartialResult(Collections.<HomeCard>emptyList(), HomeCard.CardType.SCHAMPER);
        }
        if(isTypeActive(HomeCard.CardType.NEWS_ITEM)) {
            loader.addRequest(new NewsHomeRequest(getContext(), shouldRefresh));
        } else {
            onPartialResult(Collections.<HomeCard>emptyList(), HomeCard.CardType.NEWS_ITEM);
        }
        if(isTypeActive(HomeCard.CardType.MINERVA_ANNOUNCEMENT) && AccountUtils.hasAccount(getContext())) {
            loader.addRequest(new MinervaAnnouncementRequest(getContext()));
        } else {
            onPartialResult(Collections.<HomeCard>emptyList(), HomeCard.CardType.MINERVA_ANNOUNCEMENT);
        }

        if(isTypeActive(HomeCard.CardType.MINERVA_AGENDA) && AccountUtils.hasAccount(getContext())) {
            loader.addRequest(new MinervaAgendaRequest(getContext()));
        } else {
            onPartialResult(Collections.<HomeCard>emptyList(), HomeCard.CardType.MINERVA_AGENDA);
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<IterableSparseArray<ThrowableEither<List<HomeCard>>>> loader, IterableSparseArray<ThrowableEither<List<HomeCard>>> data) {

        if(wasCached) {
            Log.d(TAG, "Received cached data");
            for(Pair<Integer, ThrowableEither<List<HomeCard>>> item: data.pairIterable()) {
                if(item.second.hasError()) {
                    //noinspection WrongConstant
                    onPartialError(item.first);
                } else {
                    //noinspection WrongConstant
                    onPartialResult(item.second.getData(), item.first);
                }
            }
        }

        wasCached = true;

        Log.i(TAG, "Finished loading data");
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<IterableSparseArray<ThrowableEither<List<HomeCard>>>> loader) {
        //Do nothing.
    }

    /**
     * Check to see if a card type is showable.
     *
     * @return True if the card may be shown.
     */
    private boolean isTypeActive(@HomeCard.CardType int cardType) {
        Set<String> data = preferences.getStringSet(HomeFeedFragment.PREF_DISABLED_CARDS, Collections.<String>emptySet());
        return !data.contains(String.valueOf(cardType));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_refresh) {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        shouldRefresh = true;
        restartLoader();
        shouldRefresh = false;
    }
}