package be.ugent.zeus.hydra.fragments.resto;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.resto.MenuActivity;
import be.ugent.zeus.hydra.activities.resto.MetaActivity;
import be.ugent.zeus.hydra.activities.resto.SandwichActivity;
import be.ugent.zeus.hydra.common.RequestHandler;
import be.ugent.zeus.hydra.common.fragments.SpiceFragment;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.requests.RestoMenuOverviewRequest;
import be.ugent.zeus.hydra.utils.DateUtils;

import java.util.ArrayList;

/**
 * @author Niko Strijbol
 * @author mivdnber
 */
public class RestoFragment extends SpiceFragment implements RequestHandler.Requester<RestoMenu> {

    public static final String FRAGMENT_TAG = "menu_today_fragment";

    private ProgressBar progressBar;
    private View layout;
    private TextView title;

    @Override
    public void onResume() {
        super.onResume();
        this.sendScreenTracking("home");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_resto, container, false);
        progressBar = (ProgressBar) layout.findViewById(R.id.progress_bar);

        layout.findViewById(R.id.home_resto_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenuActivity.class));
            }
        });

        layout.findViewById(R.id.home_resto_view_sandwich).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SandwichActivity.class));
            }
        });

        layout.findViewById(R.id.home_resto_view_resto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MetaActivity.class));
            }
        });

        title = (TextView) layout.findViewById(R.id.menu_today_card_title);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenuActivity.class));
            }
        });

        performRequest(false);

        return layout;
    }

    public void performRequest(final boolean refresh) {
        final RestoMenuOverviewRequest r = new RestoMenuOverviewRequest();

        RequestHandler.performRequest(refresh, r, this);
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
                        performRequest(false);
                    }
                })
                .show();
        hideProgressBar();
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
     * Called when the request was able to produce data.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(ArrayList<RestoMenu> data) {
        hideProgressBar();

        FragmentManager m = getChildFragmentManager();

        RestoMenu today = data.get(0);
        if(m.findFragmentByTag(FRAGMENT_TAG) == null) {
            MenuFragment fragment = MenuSingleFragment.newInstance(today);
            FragmentTransaction fragmentTransaction = m.beginTransaction();
            fragmentTransaction.add(R.id.menu_today_card_layout, fragment, FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        title.setText(String.format(getString(R.string.menu_today_title), DateUtils.getFriendlyDate(today.getDate())));
    }
}
