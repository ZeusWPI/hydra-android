package be.ugent.zeus.hydra.fragments.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.auth.AccountUtils;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * The fragment for the home tab.
 *
 * The user has the possibility to decide to hide certain card types. We still request the data, load the Loaders, etc.
 * The reason for this decision is that the user can change the preferences while the fragment is active, and this is
 * annoying for the Loaders. Also, the requested data is not lost; it is cached for the other tabs.
 *
 * @author Niko Strijbol
 * @author silox
 */
public class HomeFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, FragmentCallback {

    public static final String PREF_ACTIVE_CARDS = "pref_disabled_cards";

    private static final String TAG = "HomeFragment";

    private static final int MENU_LOADER = 1;
    private static final int ACTIVITY_LOADER = 2;
    private static final int SPECIAL_LOADER = 3;
    private static final int SCHAMPER_LOADER = 4;
    private static final int NEWS_LOADER = 5;
    private static final int MINERVA_LOADER = 6;

    private RestoMenuCallback menuCallback;
    private EventCallback eventCallback;
    private SpecialEventCallback specialEventCallback;
    private SchamperCallback schamperCallback;
    private NewsCallback newsCallback;
    private MinervaCallback courseCallback;

    private boolean shouldRefresh = false;
    private boolean preferencesUpdated = false;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = $(view, R.id.home_cards_view);
        swipeRefreshLayout = $(view, R.id.swipeRefreshLayout);
        progressBar = $(view, R.id.progress_bar);

        HomeCardAdapter adapter = new HomeCardAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shouldRefresh = true;
                restartLoaders();
                shouldRefresh = false;
            }
        });

        menuCallback = new RestoMenuCallback(getContext(), adapter, this);
        eventCallback = new EventCallback(getContext(), adapter, this);
        specialEventCallback = new SpecialEventCallback(getContext(), adapter, this);
        schamperCallback = new SchamperCallback(getContext(), adapter, this);
        newsCallback = new NewsCallback(getContext(), adapter, this);
        courseCallback = new MinervaCallback(getContext(), adapter, this);

        startLoaders();

        //Register this class in the settings.
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(preferencesUpdated) {
            restartLoaders();
            preferencesUpdated = false;
        }
    }

    /**
     * If the fragment goes to pause, we don't need to restart the loaders.
     */
    @Override
    public void onPause() {
        super.onPause();
        preferencesUpdated = false;
    }

    /**
     * Start the loaders.
     */
    private void startLoaders() {
        getLoaderManager().initLoader(MENU_LOADER, null, menuCallback);
        getLoaderManager().initLoader(ACTIVITY_LOADER, null, eventCallback);
        getLoaderManager().initLoader(SPECIAL_LOADER, null, specialEventCallback);
        getLoaderManager().initLoader(SCHAMPER_LOADER, null, schamperCallback);
        getLoaderManager().initLoader(NEWS_LOADER, null, newsCallback);
        if(AccountUtils.hasAccount(getContext())) {
            getLoaderManager().initLoader(MINERVA_LOADER, null, courseCallback);
        }
    }

    /**
     * Restart the loaders
     */
    private void restartLoaders() {
        getLoaderManager().restartLoader(MENU_LOADER, null, menuCallback);
        getLoaderManager().restartLoader(ACTIVITY_LOADER, null, eventCallback);
        getLoaderManager().restartLoader(SPECIAL_LOADER, null, specialEventCallback);
        getLoaderManager().restartLoader(SCHAMPER_LOADER, null, schamperCallback);
        getLoaderManager().restartLoader(NEWS_LOADER, null, newsCallback);
        if(AccountUtils.hasAccount(getContext())) {
            getLoaderManager().restartLoader(MINERVA_LOADER, null, courseCallback);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(PREF_ACTIVE_CARDS)) {
            preferencesUpdated = true;
        }
    }

    /**
     * Called when the loading was successfully completed.
     */
    @Override
    public void onCompleted() {
        swipeRefreshLayout.setRefreshing(false);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(String errorMessage) {
        assert getView() != null;
        progressBar.setVisibility(View.GONE);
        Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean shouldRefresh() {
        return this.shouldRefresh;
    }
}