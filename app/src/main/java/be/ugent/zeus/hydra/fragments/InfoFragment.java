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
import be.ugent.zeus.hydra.adapters.InfoListAdapter;
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.models.info.InfoList;
import be.ugent.zeus.hydra.requests.InfoRequest;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

public class InfoFragment extends LoaderFragment<InfoList> {

    public static final String INFOLIST = "infoList";

    private InfoListAdapter adapter;
    private View layout;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        RecyclerView recyclerView = $(layout, R.id.recycler_view);
        progressBar = $(layout, R.id.progress_bar);

        adapter = new InfoListAdapter();
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

    /**
     * This must be called when data is received that has no errors.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull InfoList data) {
        adapter.setItems(data);
        adapter.notifyDataSetChanged();
        hideProgressBar();
    }

    /**
     * This must be called when an error occurred.
     *
     * @param error The exception.
     */
    @Override
    public void receiveError(@NonNull Throwable error) {
        Snackbar.make(layout, getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        restartLoader();
                    }
                })
                .show();
        hideProgressBar();
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    public Request<InfoList> getRequest() {
        return new InfoRequest();
    }
}