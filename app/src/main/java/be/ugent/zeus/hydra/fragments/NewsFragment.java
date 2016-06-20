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
import be.ugent.zeus.hydra.adapters.NewsAdapter;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.models.association.News;
import be.ugent.zeus.hydra.requests.NewsRequest;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by Ellen on 07/04/2016.
 */
public class NewsFragment extends LoaderFragment<News> {

    private NewsAdapter adapter;
    private View layout;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        RecyclerView recyclerView = $(layout, R.id.recycler_view);
        progressBar = $(layout, R.id.progress_bar);

        adapter = new NewsAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        return layout;
    }

    private void showFailureSnackbar() {
        Snackbar.make(layout, "Oeps! Kon nieuws niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restartLoader();
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
    public void receiveData(@NonNull News data) {
        adapter.setItems(data);
        adapter.notifyDataSetChanged();

        progressBar.setVisibility(View.GONE);
    }

    /**
     * This must be called when an error occurred.
     *
     * @param error The exception.
     */
    @Override
    public void receiveError(@NonNull Throwable error) {
        showFailureSnackbar();
        progressBar.setVisibility(View.GONE);
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    public Request<News> getRequest() {
        return new NewsRequest();
    }
}
