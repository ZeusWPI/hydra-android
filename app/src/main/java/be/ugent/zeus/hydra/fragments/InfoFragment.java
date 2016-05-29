package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import be.ugent.zeus.hydra.common.fragments.SpiceFragment;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.InfoListAdapter;
import be.ugent.zeus.hydra.models.info.InfoItem;
import be.ugent.zeus.hydra.models.info.InfoList;
import be.ugent.zeus.hydra.requests.InfoRequest;

public class InfoFragment extends SpiceFragment {

    public static final String INFOLIST = "infoList";

    private RecyclerView recyclerView;
    private InfoListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;
    private InfoList infoItems;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_info, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview);
        progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);

        adapter = new InfoListAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Bundle bundle = getArguments();
        if (bundle != null) {
            infoItems = new InfoList();
            infoItems.addAll((ArrayList<InfoItem>)(ArrayList<? extends Parcelable>) bundle.getParcelableArrayList(INFOLIST));
            if (infoItems != null) {
                setDataSet(infoItems);
            }
        } else {
            performLoadInfoRequest();
        }

        return layout;
    }

    private void setDataSet(InfoList infoList) {
        infoItems = infoList;
        adapter.setItems(infoList);
        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
    }

    private void showFailureSnackbar() {
        Snackbar
                .make(layout, "Oeps! Kon info niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performLoadInfoRequest();
                    }
                })
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.sendScreenTracking("Info");
    }

    private void performLoadInfoRequest() {
        final InfoRequest r = new InfoRequest();

        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<InfoList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
            }

            @Override
            public void onRequestSuccess(final InfoList infolist) {
                setDataSet(infolist);
            }
        });
    }

}
