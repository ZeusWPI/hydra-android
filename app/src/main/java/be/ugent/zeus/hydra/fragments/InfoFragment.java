package be.ugent.zeus.hydra.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.AssociationActivityDetail;
import be.ugent.zeus.hydra.adapters.ActivityListAdapter;
import be.ugent.zeus.hydra.adapters.InfoListAdapter;
import be.ugent.zeus.hydra.models.Association.AssociationActivities;
import be.ugent.zeus.hydra.models.Association.AssociationActivity;
import be.ugent.zeus.hydra.models.Info.InfoItem;
import be.ugent.zeus.hydra.models.Info.InfoList;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;
import be.ugent.zeus.hydra.requests.InfoRequest;

/**
 * Created by Juta on 03/03/2016.
 */
public class InfoFragment extends AbstractFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);



        performLoadInfoRequest();

        return view;
    }

    private void performLoadInfoRequest() {

        final InfoRequest r = new InfoRequest();
        spiceManager.execute(r, r.getCacheKey(), r.getCacheDuration(), new RequestListener<InfoList>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                System.out.println("Request failed");
            }

            @Override
            public void onRequestSuccess(final InfoList infolist) {
                ArrayList<InfoItem> listItems = new ArrayList<>();
                ArrayAdapter<InfoItem> adapter;
                final ListView activityList = (ListView) getView().findViewById(R.id.infoList);
                adapter = new InfoListAdapter(getContext(), android.R.layout.simple_list_item_1, listItems);
                activityList.setAdapter(adapter);

                activityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //TODO
                    }
                });

                for (InfoItem item : infolist) {
                    listItems.add(item);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}
