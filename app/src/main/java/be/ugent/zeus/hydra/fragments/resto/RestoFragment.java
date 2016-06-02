package be.ugent.zeus.hydra.fragments.resto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import be.ugent.zeus.hydra.fragments.common.LoaderFragment;
import be.ugent.zeus.hydra.fragments.resto.menu.MenuFragment;
import be.ugent.zeus.hydra.fragments.resto.menu.MenuSingleFragment;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.requests.RestoMenuOverviewRequest;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * @author Niko Strijbol
 * @author mivdnber
 */
public class RestoFragment extends LoaderFragment<RestoOverview> {

    private static final String FRAGMENT_TAG = "menu_today_fragment";

    private ProgressBar progressBar;
    private View layout;
    private TextView title;

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

        layout.findViewById(R.id.menu_today_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MenuActivity.class));
            }
        });

        startLoader();

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
     * This must be called when data is received that has no errors.
     *
     * @param data The data.
     */
    @Override
    public void receiveData(@NonNull RestoOverview data) {
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
    public Request<RestoOverview> getRequest() {
        return new RestoMenuOverviewRequest();
    }
}