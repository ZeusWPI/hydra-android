package be.ugent.zeus.hydra.fragments.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.preferences.AssociationSelectPrefActivity;
import be.ugent.zeus.hydra.fragments.home.loader.HomeFeedLoader;
import be.ugent.zeus.hydra.fragments.home.loader.HomeFeedLoaderCallback;
import be.ugent.zeus.hydra.fragments.home.requests.*;
import be.ugent.zeus.hydra.minerva.auth.AccountUtils;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.utils.recycler.SpanItemSpacingDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static be.ugent.zeus.hydra.fragments.home.operations.RemoveOperation.del;
import static be.ugent.zeus.hydra.fragments.home.operations.RequestOperation.add;
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
    private RecyclerView recyclerView;
    private Snackbar snackbar;

    /**
     * This boolean indicates whether the data from the loader was cached or not. If it was, the partial update
     * function will not be called.
     */
    private boolean wasCached = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = $(view, R.id.home_cards_view);
        recyclerView.setHasFixedSize(true);
        swipeRefreshLayout = $(view, R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);

        adapter = new HomeCardAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
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
    public List<HomeCard> getExistingData() {
        return adapter.getData();
    }

    @Override
    public void onPartialUpdate(List<HomeCard> data, @Nullable DiffUtil.DiffResult update, @HomeCard.CardType int cardType) {
        Log.i(TAG, "Added card type: " + cardType);
        adapter.setData(data, update);
        wasCached = false;
        if(!shouldRefresh) {
            recyclerView.scrollToPosition(0);
        }
    }

    public HomeFeedLoader getLoader() {
        return (HomeFeedLoader) getLoaderManager().<Pair<Set<Integer>, List<HomeCard>>>getLoader(LOADER);
    }

    @Override
    public void onPartialError(@HomeCard.CardType int cardType) {
        showErrorMessage("Kon sommige dingen niet ophalen.");
    }

    @Override
    public Loader<Pair<Set<Integer>, List<HomeCard>>> onCreateLoader(int id, Bundle args) {

        HomeFeedLoader loader = new HomeFeedLoader(getContext(), this);

        Set<String> s = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getStringSet(HomeFeedFragment.PREF_DISABLED_CARDS, Collections.emptySet());

        //Always add the special events.
        loader.addOperation(add(new SpecialEventRequest(getContext(), shouldRefresh)));

        if(isTypeActive(s, HomeCard.CardType.RESTO)) {
            loader.addOperation(add(new RestoRequest(getContext(), shouldRefresh)));
        } else {
            loader.addOperation(del(HomeCard.CardType.RESTO));
        }

        if(isTypeActive(s, HomeCard.CardType.ACTIVITY)) {
            loader.addOperation(add(new EventRequest(getContext(), shouldRefresh)));
        } else {
            loader.addOperation(del(HomeCard.CardType.ACTIVITY));
        }

        if(isTypeActive(s, HomeCard.CardType.SCHAMPER)) {
            loader.addOperation(add(new SchamperRequest(getContext(), shouldRefresh)));
        } else {
            loader.addOperation(del(HomeCard.CardType.SCHAMPER));
        }

        if(isTypeActive(s, HomeCard.CardType.NEWS_ITEM)) {
            loader.addOperation(add(new NewsHomeRequest(getContext(), shouldRefresh)));
        } else {
            loader.addOperation(del(HomeCard.CardType.NEWS_ITEM));
        }

        if(isTypeActive(s, HomeCard.CardType.MINERVA_ANNOUNCEMENT) && AccountUtils.hasAccount(getContext())) {
            loader.addOperation(add(new MinervaAnnouncementRequest(getContext())));
        } else {
            loader.addOperation(del(HomeCard.CardType.MINERVA_ANNOUNCEMENT));
        }

        if(isTypeActive(s, HomeCard.CardType.MINERVA_AGENDA) && AccountUtils.hasAccount(getContext())) {
            loader.addOperation(add(new MinervaAgendaRequest(getContext())));
        } else {
            loader.addOperation(del(HomeCard.CardType.MINERVA_AGENDA));
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Pair<Set<Integer>, List<HomeCard>>> l, Pair<Set<Integer>, List<HomeCard>> data) {
        Log.i(TAG, "Finished loading data");
        if(wasCached) {
            for(Integer error: data.first) {
                //noinspection WrongConstant
                onPartialError(error);
            }

            adapter.setData(new ArrayList<>(data.second), null);
        }
        wasCached = true;
        shouldRefresh = false;
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<Pair<Set<Integer>, List<HomeCard>>> loader) {
        //Do nothing
    }

    /**
     * Check to see if a card type is showable.
     *
     * @return True if the card may be shown.
     */
    private boolean isTypeActive(Set<String> data, @HomeCard.CardType int cardType) {
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
    }
}