package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.Association.AssociationActivities;
import be.ugent.zeus.hydra.models.Association.AssociationActivity;
import be.ugent.zeus.hydra.models.Association.AssociationNews;
import be.ugent.zeus.hydra.models.Association.AssociationNewsItem;
import be.ugent.zeus.hydra.models.Resto.RestoItem;
import be.ugent.zeus.hydra.models.Resto.RestoMenu;
import be.ugent.zeus.hydra.requests.AssociationActivitiesRequest;
import be.ugent.zeus.hydra.requests.AssociationNewsRequest;

/**
 * Created by silox on 17/10/15.
 */

public class HomeFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.homefragment_view, container, false);

        TextView groenten = (TextView) view.findViewById(R.id.groenten);
        //groenten.setText("hello world");

        return view;
    }


}
