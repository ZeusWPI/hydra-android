package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.InfoListAdapter;
import be.ugent.zeus.hydra.models.info.InfoList;
import be.ugent.zeus.hydra.requests.InfoRequest;

public class InfoFragment extends AbstractFragment {

    private RecyclerView recyclerView;
    private InfoListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_activities, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview);
        adapter = new InfoListAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        performLoadInfoRequest();

        return layout;
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


    private void performLoadInfoRequest() {
        final InfoRequest r = new InfoRequest();

        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<InfoList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
            }

            @Override
            public void onRequestSuccess(final InfoList infolist) {
                adapter.setItems(infolist);
                adapter.notifyDataSetChanged();
            }
        });


    }

}
