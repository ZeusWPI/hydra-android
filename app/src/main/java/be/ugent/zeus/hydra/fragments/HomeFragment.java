package be.ugent.zeus.hydra.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loader.LoaderCallback;
import be.ugent.zeus.hydra.loader.ThrowableEither;
import be.ugent.zeus.hydra.loader.cache.CacheRequest;
import be.ugent.zeus.hydra.models.association.Activities;
import be.ugent.zeus.hydra.models.association.Activity;
import be.ugent.zeus.hydra.models.association.News;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.models.cards.*;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.ActivitiesRequest;
import be.ugent.zeus.hydra.requests.NewsRequest;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.requests.SpecialEventRequest;
import be.ugent.zeus.hydra.requests.resto.RestoMenuOverviewRequest;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.*;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * The fragment for the home tab.
 *
 * The user has the possibility to decide to hide certain card types. We still request the data, load the Loaders, etc.
 * The reason for this decision is that the user can change the preferences while the fragment is active, and this is
 * annoying for the Loaders. Also, the requested data is not lost; it is cached for the other tabs.
 *
 * @author Niko Strijbol
 * @author silox
 */
public class HomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final boolean DEVELOPMENT = true;

    private static final int MENU_LOADER = 1;
    private static final int ACTIVITY_LOADER = 2;
    private static final int SPECIAL_LOADER = 3;
    private static final int SCHAMPER_LOADER = 4;
    private static final int NEWS_LOADER = 5;

    private final MenuCallback menuCallback = new MenuCallback();
    private final ActivityCallback activityCallback = new ActivityCallback();
    private final SpecialEventCallback specialEventCallback = new SpecialEventCallback();
    private final SchamperCallback schamperCallback = new SchamperCallback();
    private final NewsCallback newsCallback = new NewsCallback();

    private boolean shouldRefresh = false;
    private boolean preferencesUpdated = false;

    private HomeCardAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = $(view, R.id.home_cards_view);
        swipeRefreshLayout = $(view, R.id.swipeRefreshLayout);
        progressBar = $(view, R.id.progress_bar);

        adapter = new HomeCardAdapter(PreferenceManager.getDefaultSharedPreferences(getActivity()));
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shouldRefresh = true;
                restartLoaders();
                shouldRefresh = false;
            }
        });

        startLoaders();

        //Register this class in the settings.
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    private boolean isTypeActive(@HomeCard.CardType int cardType) {
        Set<String> data = PreferenceManager.getDefaultSharedPreferences(getActivity()).getStringSet("pref_disabled_cards", Collections.<String>emptySet());
        return !data.contains(String.valueOf(cardType));
    }

    @Override
    public void onResume() {
        super.onResume();

        if(preferencesUpdated) {
            restartLoaders();
            preferencesUpdated = false;
        }
    }

    /**
     * If the fragment goes to pauze, we don't need to restart the loaders.
     */
    @Override
    public void onPause() {
        super.onPause();
        preferencesUpdated = false;
    }

    /**
     * Start the loaders.
     */
    private void startLoaders() {
        getLoaderManager().initLoader(MENU_LOADER, null, menuCallback);
        getLoaderManager().initLoader(ACTIVITY_LOADER, null, activityCallback);
        getLoaderManager().initLoader(SPECIAL_LOADER, null, specialEventCallback);
        getLoaderManager().initLoader(SCHAMPER_LOADER, null, schamperCallback);
        getLoaderManager().initLoader(NEWS_LOADER, null, newsCallback);
    }

    /**
     * Restart the loaders
     */
    private void restartLoaders() {
        getLoaderManager().restartLoader(MENU_LOADER, null, menuCallback);
        getLoaderManager().restartLoader(ACTIVITY_LOADER, null, activityCallback);
        getLoaderManager().restartLoader(SPECIAL_LOADER, null, specialEventCallback);
        getLoaderManager().restartLoader(SCHAMPER_LOADER, null, schamperCallback);
        getLoaderManager().restartLoader(NEWS_LOADER, null, newsCallback);
    }

    /**
     * When one of the loaders is complete.
     */
    private void loadComplete() {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Show a snack bar.
     *
     * @param field The failing field.
     */
    private void showFailureSnackbar(String field) {
        assert getView() != null;
        Snackbar.make(getView(), "Oeps! Kon " + field + " niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shouldRefresh = true;
                        restartLoaders();
                        shouldRefresh = false;
                    }
                })
                .show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals("pref_disabled_cards")) {
            preferencesUpdated = true;
        }
    }

    private abstract class AbstractLoaderCallback<D extends Serializable> extends LoaderCallback<D> {
        @Override
        public Loader<ThrowableEither<D>> onCreateLoader(int id, Bundle args) {
            return super.onCreateLoader(getContext(), shouldRefresh);
        }
    }

    private class MenuCallback extends LoaderCallback<RestoOverview> {

        /**
         * This must be called when data is received that has no errors.
         *
         * @param data The data.
         */
        @Override
        public void receiveData(@NonNull RestoOverview data) {

            if(!isTypeActive(HomeCard.CardType.RESTO)) {
                return;
            }

            List<HomeCard> menuCardList = new ArrayList<>();
            for (RestoMenu menu : data) {
                if (new DateTime(menu.getDate()).withTimeAtStartOfDay().isAfterNow()) {
                    menuCardList.add(new RestoMenuCard(menu)); //TODO: add current day
                }
            }
            adapter.updateCardItems(menuCardList, HomeCard.CardType.RESTO);
            loadComplete();
        }

        /**
         * This must be called when an error occurred.
         *
         * @param error The exception.
         */
        @Override
        public void receiveError(@NonNull Throwable error) {
            if(!isTypeActive(HomeCard.CardType.RESTO)) {
                return;
            }
            showFailureSnackbar("restomenu");
            loadComplete();
        }

        /**
         * @return The request that will be executed.
         */
        @Override
        public CacheRequest<RestoOverview> getRequest() {
            return new RestoMenuOverviewRequest();
        }

        @Override
        public Loader<ThrowableEither<RestoOverview>> onCreateLoader(int id, Bundle args) {
            return super.onCreateLoader(getContext(), shouldRefresh);
        }
    }

    private class ActivityCallback extends AbstractLoaderCallback<Activities> {

        /**
         * This must be called when data is received that has no errors.
         *
         * @param data The data.
         */
        @Override
        public void receiveData(@NonNull Activities data) {
            if(!isTypeActive(HomeCard.CardType.ACTIVITY)) {
                return;
            }
            List<Activity> filteredAssociationActivities = Activities.getPreferredActivities(data, getContext());
            Date date = new Date();
            List<HomeCard> list = new ArrayList<>();
            for (Activity activity: filteredAssociationActivities) {
                AssociationActivityCard activityCard = new AssociationActivityCard(activity);
                if(activityCard.getPriority() > 0 && activity.getEndDate().after(date)) {
                    list.add(activityCard);
                }
            }
            adapter.updateCardItems(list, HomeCard.CardType.ACTIVITY);
            loadComplete();
        }

        /**
         * This must be called when an error occurred.
         *
         * @param error The exception.
         */
        @Override
        public void receiveError(@NonNull Throwable error) {
            if(!isTypeActive(HomeCard.CardType.ACTIVITY)) {
                return;
            }
            showFailureSnackbar("activiteiten");
            loadComplete();
        }

        /**
         * @return The request that will be executed.
         */
        @Override
        public CacheRequest<Activities> getRequest() {
            return new ActivitiesRequest();
        }
    }

    /**
     * Note: you cannot hide special event.
     */
    private class SpecialEventCallback extends AbstractLoaderCallback<SpecialEventWrapper> {

        /**
         * This must be called when data is received that has no errors.
         *
         * @param data The data.
         */
        @Override
        public void receiveData(@NonNull SpecialEventWrapper data) {
            List<HomeCard> list = new ArrayList<>();
            for (SpecialEvent event: data.getSpecialEvents()) {
                Date now = new Date();
                if ((event.getStart().before(now) && event.getEnd().after(now)) || (DEVELOPMENT && event.isDevelopment())) {
                    list.add(new SpecialEventCard(event));
                }
            }

            adapter.updateCardItems(list, HomeCard.CardType.SPECIAL_EVENT);
            loadComplete();
        }

        /**
         * This must be called when an error occurred.
         *
         * @param error The exception.
         */
        @Override
        public void receiveError(@NonNull Throwable error) {
            showFailureSnackbar("speciale activiteiten");
            loadComplete();
        }

        /**
         * @return The request that will be executed.
         */
        @Override
        public CacheRequest<SpecialEventWrapper> getRequest() {
            return new SpecialEventRequest();
        }
    }

    private class SchamperCallback extends AbstractLoaderCallback<Articles> {

        /**
         * This must be called when data is received that has no errors.
         *
         * @param data The data.
         */
        @Override
        public void receiveData(@NonNull Articles data) {
            if(!isTypeActive(HomeCard.CardType.SCHAMPER)) {
                return;
            }
            List<HomeCard> schamperCardList = new ArrayList<>();
            for (Article article : data) {
                schamperCardList.add(new SchamperCard(article));
            }
            adapter.updateCardItems(schamperCardList, HomeCard.CardType.SCHAMPER);
            loadComplete();
        }

        /**
         * This must be called when an error occurred.
         *
         * @param error The exception.
         */
        @Override
        public void receiveError(@NonNull Throwable error) {
            if(!isTypeActive(HomeCard.CardType.SCHAMPER)) {
                return;
            }
            showFailureSnackbar("schamper");
            loadComplete();
        }

        /**
         * @return The request that will be executed.
         */
        @Override
        public CacheRequest<Articles> getRequest() {
            return new SchamperArticlesRequest();
        }
    }

    private class NewsCallback extends AbstractLoaderCallback<News> {

        /**
         * This must be called when data is received that has no errors.
         *
         * @param data The data.
         */
        @Override
        public void receiveData(@NonNull News data) {
            if (!isTypeActive(HomeCard.CardType.NEWS_ITEM)) {
                return;
            }

            List<HomeCard> newsItemCardList = new ArrayList<>();
            DateTime now = DateTime.now();
            DateTime sixMonthsAgo = now.minusMonths(6);

            for (NewsItem item : data) {
                if (sixMonthsAgo.isBefore(item.getDate().getTime())) {
                    newsItemCardList.add(new NewsItemCard(item));
                }
            }

            adapter.updateCardItems(newsItemCardList, HomeCard.CardType.NEWS_ITEM);
            loadComplete();
        }

        /**
         * This must be called when an error occurred.
         *
         * @param error The exception.
         */
        @Override
        public void receiveError(@NonNull Throwable error) {
            if (!isTypeActive(HomeCard.CardType.NEWS_ITEM)) {
                return;
            }
            showFailureSnackbar("news items");
            loadComplete();
        }

        /**
         * @return The request that will be executed.
         */
        @Override
        public CacheRequest<News> getRequest() {
            return new NewsRequest();
        }
    }

//    private void performMinervaTestLoggedInRequest() {
//        adapter.updateCardItems(new ArrayList<HomeCard>(), HomeCard.CardType.MINERVA_LOGIN);
//        if (!authorizationManager.isAuthenticated()) {
//            if(authorizationManager.getCurrentToken() != null) {
//                // previously logged in, try to authorize again
//                minervaSpiceManager.execute(authorizationManager.buildTokenRefreshRequest(), new RequestListener<BearerToken>() {
//                    @Override
//                    public void onRequestFailure(SpiceException spiceException) {
//                        // Not possible to authorize, so user should authorize possibly again
//                        List<HomeCard> list = new ArrayList<>();
//                        list.add(new MinervaLoginCard());
//                        adapter.updateCardItems(list, HomeCard.CardType.MINERVA_LOGIN);
//                    }
//
//                    @Override
//                    public void onRequestSuccess(BearerToken bearerToken) {
//                        authorizationManager.setBearerToken(bearerToken);
//                    }
//                });
//            } else {
//                // Not logged in
//                List<HomeCard> list = new ArrayList<>();
//                list.add(new MinervaLoginCard());
//                adapter.updateCardItems(list, HomeCard.CardType.MINERVA_LOGIN);
//            }
//        }
//    }

}