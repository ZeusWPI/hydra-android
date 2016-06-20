package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.SchamperListAdapter;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperFragment extends LoaderFragment<Articles> {

    private SchamperListAdapter adapter;
    private View layout;
    private ProgressBar progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_schamper_articles, container, false);
        RecyclerView recyclerView = $(layout, R.id.recycler_view);
        progressBar = $(layout, R.id.progress_bar);

        adapter = new SchamperListAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return layout;
    }

    /**
     * Hide the progress bar.
     */
    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showFailureSnackbar() {
        Snackbar.make(layout, "Oeps! Kon de schamperartikelen niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shouldRenew = true;
                        restartLoader();
                        shouldRenew = false;
                    }
                })
                .show();
    }

    /**
     * This must be called when data is received that has no errors.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull Articles data) {
        adapter.setArticles(data);
        hideProgressBar();
    }

    /**
     * This must be called when an error occurred.
     *
     * @param error The exception.
     */
    @Override
    public void receiveError(@NonNull Throwable error) {
        showFailureSnackbar();
        hideProgressBar();
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    public Request<Articles> getRequest() {
        return new SchamperArticlesRequest();
    }
}
