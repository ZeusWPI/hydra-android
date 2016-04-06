package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.adapters.RestoCardAdapter;
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.models.association.AssociationActivities;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoMenuList;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;
import be.ugent.zeus.hydra.requests.RestoMenuOverviewRequest;
import be.ugent.zeus.hydra.requests.SpecialEventRequest;

/**
 * Created by silox on 17/10/15.
 */

public class HomeFragment extends AbstractFragment {
    private RecyclerView recyclerView;
    private HomeCardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.homefragment_view, container, false);

        recyclerView = (RecyclerView) layout.findViewById(R.id.home_cards_view);
        adapter = new HomeCardAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        performRequest();

        return layout;
    }

    private void performRequest() {
        performMenuRequest();
        performActivityRequest();
        performSpecialEventRequest();
    }

    private void performMenuRequest() {
        final RestoMenuOverviewRequest r = new RestoMenuOverviewRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<RestoMenuList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar("restomenu");
            }

            @Override
            public void onRequestSuccess(final RestoMenuList menuList) {
                ArrayList<HomeCard> list = new ArrayList<>();
                //list.addAll(menuList); //Why no casting :'(
                for (RestoMenu menu: menuList) {
                    if (new DateTime(menu.getDate()).withTimeAtStartOfDay().isAfterNow()) {
                        list.add(menu); //TODO: add current day
                    }
                }
                adapter.updateCardItems(list, HomeCardAdapter.HomeType.RESTO);
            }
        });

    }

    private void performActivityRequest() {
        final AssociationActivitiesRequest r = new AssociationActivitiesRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<AssociationActivities>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar("activiteiten");
            }

            @Override
            public void onRequestSuccess(final AssociationActivities associationActivities) {
                List<HomeCard> list = new ArrayList<>();
                for (AssociationActivity activity: associationActivities) {
                    if(activity.getPriority() > 0) {
                        list.add(activity);
                    }
                }
                adapter.updateCardItems(list, HomeCardAdapter.HomeType.ACTIVITY);
            }
        });
    }

    private void performSpecialEventRequest() {
        final SpecialEventRequest r = new SpecialEventRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<SpecialEventWrapper>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar("speciale activiteiten");
            }

            @Override
            public void onRequestSuccess(SpecialEventWrapper specialEventWrapper) {
                List<HomeCard> list = new ArrayList<>();
                boolean development_enabled = true;
                for (SpecialEvent event: specialEventWrapper.getSpecialEvents()) {
                    if ((event.getStart().before(new Date()) && event.getEnd().after(new Date())) || (development_enabled && event.isDevelopment())) {
                        list.add(event);
                    }
                }

                adapter.updateCardItems(list, HomeCardAdapter.HomeType.SPECIALEVENT);
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
