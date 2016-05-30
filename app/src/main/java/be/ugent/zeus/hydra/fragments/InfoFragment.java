package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.InfoListAdapter;
import be.ugent.zeus.hydra.common.RequestHandler;
import be.ugent.zeus.hydra.common.fragments.SpiceFragment;
import be.ugent.zeus.hydra.models.info.InfoItem;
import be.ugent.zeus.hydra.requests.InfoRequest;

import java.util.ArrayList;

public class InfoFragment extends SpiceFragment implements RequestHandler.Requester<ArrayList<InfoItem>> {

    public static final String INFOLIST = "infoList";

    private InfoListAdapter adapter;
    private View layout;
    private ArrayList<InfoItem> infoItems = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_info, container, false);
        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview);
        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

        adapter = new InfoListAdapter();
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Bundle bundle = getArguments();
        if (bundle != null) {
            infoItems = bundle.getParcelableArrayList(INFOLIST);
            if (infoItems != null) {
                receiveData(infoItems);
            }
        } else {
            performRequest(false);
        }

        return layout;
    }

    /**
     * Hide the progress bar.
     */
    private void hideProgressBar() {
        if(progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Called when the requests has failed.
     */
    @Override
    public void requestFailure() {
        Snackbar.make(layout, getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performRequest(true);
                    }
                })
                .show();
        hideProgressBar();
    }

    @Override
    public void performRequest(boolean refresh) {
        RequestHandler.performListRequest(refresh, new InfoRequest(), this);
    }

    /**
     * Called when the request was able to produce data.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(ArrayList<InfoItem> data) {
        infoItems = data;
        adapter.setItems(data);
        adapter.notifyDataSetChanged();
        hideProgressBar();
    }
}