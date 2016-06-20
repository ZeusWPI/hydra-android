package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.cards.AssociationActivityCard;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.association.AssociationActivities;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import be.ugent.zeus.hydra.models.cards.RestoMenuCard;
import be.ugent.zeus.hydra.models.cards.SchamperCard;
import be.ugent.zeus.hydra.models.cards.SpecialEventCard;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoMenuList;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;
import be.ugent.zeus.hydra.requests.RestoMenuOverviewRequest;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.requests.SpecialEventRequest;

/**
 * Created by silox on 17/10/15.
 */

public class HomeFragment extends AbstractFragment {
    private RecyclerView recyclerView;
    private HomeCardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    @Override
    public void onResume() {
        super.onResume();
        this.sendScreenTracking("Home");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.homefragment_view, container, false);

        recyclerView = (RecyclerView) layout.findViewById(R.id.home_cards_view);
        swipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipeRefreshLayout);
        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

        adapter = new HomeCardAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                performRequest();
            }
        });

        performRequest();

        return layout;
    }

    private void performRequest() {
        performMenuRequest();
        performActivityRequest();
        performSpecialEventRequest();
        performSchamperRequest();
    }

    private void loadComplete() {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    private void performMenuRequest() {
        final RestoMenuOverviewRequest r = new RestoMenuOverviewRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<RestoMenuList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar("restomenu");
                loadComplete();
            }

            @Override
            public void onRequestSuccess(final RestoMenuList menuList) {
                List<HomeCard> menuCardList = new ArrayList<>();
                for (RestoMenu menu : menuList) {
                    // only show till 20 o'clock
                    if (new DateTime(menu.getDate()).withTimeAtStartOfDay().plusHours(20).isAfterNow()) {
                        menuCardList.add(new RestoMenuCard(menu));
                    }
                }
                adapter.updateCardItems(menuCardList, HomeCardAdapter.HomeType.RESTO);
                loadComplete();
            }
        });

    }

    private void performActivityRequest() {
        final AssociationActivitiesRequest r = new AssociationActivitiesRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<AssociationActivities>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar("activiteiten");
                loadComplete();
            }

            @Override
            public void onRequestSuccess(final AssociationActivities associationActivities) {
                List<HomeCard> activityCardList = new ArrayList<>();
                AssociationActivities filteredActivities = associationActivities.getPreferedActivities(getContext());
                Date date = new Date();

                for (AssociationActivity activity : filteredActivities) {
                    AssociationActivityCard activityCard = new AssociationActivityCard(activity);
                    if(activityCard.getPriority() > 0 && activity.getEndDate().after(date)) {
                        activityCardList.add(activityCard);
                    }
                }
                adapter.updateCardItems(activityCardList, HomeCardAdapter.HomeType.ACTIVITY);
                loadComplete();
            }
        });
    }

    private void performSpecialEventRequest() {
        final SpecialEventRequest r = new SpecialEventRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<SpecialEventWrapper>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar("speciale activiteiten");
                loadComplete();
            }

            @Override
            public void onRequestSuccess(SpecialEventWrapper specialEventWrapper) {
                List<HomeCard> list = new ArrayList<>();
                boolean development_enabled = true;
                for (SpecialEvent event: specialEventWrapper.getSpecialEvents()) {
                    if ((event.getStart().before(new Date()) && event.getEnd().after(new Date())) || (development_enabled && event.isDevelopment())) {
                        list.add(new SpecialEventCard(event));
                    }
                }

                adapter.updateCardItems(list, HomeCardAdapter.HomeType.SPECIALEVENT);
                loadComplete();
            }
        });
    }

    private void performSchamperRequest() {
        final SchamperArticlesRequest r = new SchamperArticlesRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<Articles>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar("schamper");
            }

            @Override
            public void onRequestSuccess(final Articles articles) {
                List<HomeCard> schamperCardList = new ArrayList<>();
                for (Article article : articles) {
                    schamperCardList.add(new SchamperCard(article));
                }
                adapter.updateCardItems(schamperCardList, HomeCardAdapter.HomeType.SCHAMPER);
                loadComplete();
            }
        });
    }

    private void showFailureSnackbar(String field) {
        Snackbar
                .make(layout, "Oeps! Kon " + field + " niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performRequest();
                    }
                })
                .show();
    }
}
