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

import java.util.ArrayList;
import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.adapters.RestoCardAdapter;
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.models.resto.RestoMenuList;
import be.ugent.zeus.hydra.requests.RestoMenuOverviewRequest;

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
                ArrayList<HomeCard> list = new ArrayList<HomeCard>();
                list.addAll(menuList); //Why no casting :'(
                adapter.updateCardItems(list, HomeCardAdapter.HomeType.RESTO);
            }
        });

    }

    private void showFailureSnackbar(String field) {
        Snackbar
                .make(layout, "Oeps! Kon " + field + " niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performMenuRequest();
                    }
                })
                .show();
    }
}
