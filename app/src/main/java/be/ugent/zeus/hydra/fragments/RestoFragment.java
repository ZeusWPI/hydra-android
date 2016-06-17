package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.RestoCardAdapter;
import be.ugent.zeus.hydra.models.resto.RestoMenuList;
import be.ugent.zeus.hydra.requests.RestoMenuOverviewRequest;

/**
 * Created by mivdnber on 2016-03-03.
 */

public class RestoFragment extends AbstractFragment {
    private RecyclerView recyclerView;
    private RestoCardAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;
    private ProgressBar progressBar;

    @Override
    public void onResume() {
        super.onResume();
        this.sendScreenTracking("home");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_resto, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.resto_cards_view);
        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        adapter = new RestoCardAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new StickyRecyclerHeadersDecoration((StickyRecyclerHeadersAdapter) adapter));
        performMenuRequest();

        return layout;
    }

    private void performMenuRequest() {
        final RestoMenuOverviewRequest r = new RestoMenuOverviewRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<RestoMenuList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onRequestSuccess(final RestoMenuList menuList) {
                adapter.setMenuList(menuList);
                adapter.notifyDataSetChanged();
                if (menuList.isEmpty()) {
                    showFailureSnackbar(); //TODO: let user now the menus are empty
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showFailureSnackbar() {
        Snackbar
            .make(layout, "Oeps! Kon restomenu niet ophalen.", Snackbar.LENGTH_LONG)
            .setAction("Opnieuw proberen", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performMenuRequest();
                }
            })
            .show();
    }
}
