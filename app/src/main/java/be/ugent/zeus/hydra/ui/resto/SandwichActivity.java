package be.ugent.zeus.hydra.ui.resto;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.observers.AdapterObserver;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

/**
 * Activity that shows a list of sandwiches.
 *
 * @author Niko Strijbol
 */
public class SandwichActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "SandwichActivity";
    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto/broodjes/overzicht.htm";

    private final SandwichAdapter adapter = new SandwichAdapter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_sandwich);

        RecyclerView recyclerView = $(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerFastScroller s = $(R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = $(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);
        swipeRefreshLayout.setOnRefreshListener(this);

        SandwichViewModel model = ViewModelProviders.of(this).get(SandwichViewModel.class);
        model.getData().observe(this, ErrorObserver.with(this::onError));
        model.getData().observe(this, new ProgressObserver<>($(R.id.progress_bar)));
        model.getData().observe(this, new AdapterObserver<>(adapter));
        model.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                onRefresh();
                return true;
            case R.id.resto_show_website:
                NetworkUtils.maybeLaunchBrowser(this, URL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resto, menu);
        tintToolbarIcons(menu, R.id.action_refresh);
        return true;
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make($(android.R.id.content), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> onRefresh())
                .show();
    }

    public void onRefresh() {
        RefreshBroadcast.broadcastRefresh(this, true);
    }
}