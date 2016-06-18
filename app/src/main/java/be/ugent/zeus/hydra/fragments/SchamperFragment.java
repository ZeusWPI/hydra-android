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
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.SchamperListAdapter;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperFragment extends AbstractFragment {
    private RecyclerView recyclerView;
    private SchamperListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;
    private ProgressBar progressBar;

    @Override
    public void onResume() {
        super.onResume();
        this.sendScreenTracking("schamper");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_schamper_articles, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview);
        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

        adapter = new SchamperListAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        performLoadArticlesRequest();

        return layout;
    }

    private void performLoadArticlesRequest() {
        final SchamperArticlesRequest r = new SchamperArticlesRequest();
        r.execute(spiceManager, new RequestListener<Articles>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onRequestSuccess(Articles articles) {
                adapter.setArticles(articles);
                adapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showFailureSnackbar() {
        Snackbar
                .make(layout, "Oeps! Kon de schamper articles niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performLoadArticlesRequest();
                    }
                })
                .show();
    }
}
