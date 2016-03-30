package be.ugent.zeus.hydra.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.adapters.ActivityListAdapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import be.ugent.zeus.hydra.activities.AssociationActivityDetail;
import be.ugent.zeus.hydra.models.association.AssociationActivities;
import be.ugent.zeus.hydra.models.association.AssociationActivity;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;

/**
 * Created by ellen on 2016-03-08.
 */

public class ActivitiesFragment extends AbstractFragment {
    private RecyclerView recyclerView;
    private ActivityListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private View layout;
    private StickyRecyclerHeadersDecoration decorator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_activities, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.recyclerview);
        adapter = new ActivityListAdapter();
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        decorator = new StickyRecyclerHeadersDecoration((StickyRecyclerHeadersAdapter) adapter);
        recyclerView.addItemDecoration(decorator);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activities, container, false);

        performLoadActivityRequest();

        return layout;
    }



    private void performLoadActivityRequest() {

        final AssociationActivitiesRequest r = new AssociationActivitiesRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<AssociationActivities>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                showFailureSnackbar();
            }

            @Override
            public void onRequestSuccess(final AssociationActivities associationActivitiesItems) {
                adapter.setItems(associationActivitiesItems);
                adapter.notifyDataSetChanged();
                ArrayList<String> listItems = new ArrayList<String>();
                ArrayAdapter<String> adapter;
                final ListView activityList = (ListView) getView().findViewById(R.id.activityList);
                adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, listItems);
                activityList.setAdapter(adapter);

                activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        int itemPosition = position;
                        Intent intent = new Intent(getContext(), AssociationActivityDetail.class);
                        intent.putExtra("associationActivity", associationActivitiesItems.get(itemPosition));
                        startActivity(intent);
                        //Toast.makeText(getContext(),
                        //        "Position :" + itemPosition + "  ListItem : " + associationActivitiesItems.get(itemPosition).title, Toast.LENGTH_LONG)
                        //        .show();
                    }
                });

                for (AssociationActivity activity : associationActivitiesItems) {
                    listItems.add(activity.title);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }


    private void showFailureSnackbar() {
        Snackbar
            .make(layout, "Oeps! Kon activiteiten niet ophalen.", Snackbar.LENGTH_LONG)
            .setAction("Opnieuw proberen", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performLoadActivityRequest();
                }
            })
            .show();
    }
}
