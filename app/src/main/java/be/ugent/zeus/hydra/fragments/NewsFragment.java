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
import be.ugent.zeus.hydra.adapters.NewsAdapter;
import be.ugent.zeus.hydra.models.association.AssociationNews;
import be.ugent.zeus.hydra.requests.AssociationNewsRequest;

/**
 * Created by Ellen on 07/04/2016.
 */
public class NewsFragment extends AbstractFragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_activities, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview);
        adapter = new NewsAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);

        performLoadActivityRequest();

        return layout;
    }



    private void performLoadActivityRequest() {

        final AssociationNewsRequest r = new AssociationNewsRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<AssociationNews>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
            }

            @Override
            public void onRequestSuccess(final AssociationNews AssociationNews) {
                adapter.setItems(AssociationNews);
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void showFailureSnackbar() {
        Snackbar
                .make(layout, "Oeps! Kon nieuws niet ophalen.", Snackbar.LENGTH_LONG)
                .setAction("Opnieuw proberen", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performLoadActivityRequest();
                    }
                })
                .show();
    }
}
