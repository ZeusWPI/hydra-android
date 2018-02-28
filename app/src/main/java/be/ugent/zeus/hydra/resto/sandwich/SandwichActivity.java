package be.ugent.zeus.hydra.resto.sandwich;

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
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.ErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

/**
 * Activity that shows a list of sandwiches.
 *
 * @author Niko Strijbol
 */
public class SandwichActivity extends BaseActivity {

    private static final String TAG = "SandwichActivity";
    private static final String URL = "http://www.ugent.be/student/nl/meer-dan-studeren/resto/broodjes/overzicht.htm";

    private final SandwichAdapter adapter = new SandwichAdapter();
    private SandwichViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_sandwich);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        RecyclerFastScroller s = findViewById(R.id.fast_scroller);
        s.attachRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ugent_yellow_dark);

        viewModel = ViewModelProviders.of(this).get(SandwichViewModel.class);
        viewModel.getData().observe(this, ErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(findViewById(R.id.progress_bar)));
        viewModel.getData().observe(this, new AdapterObserver<>(adapter));
        viewModel.getRefreshing().observe(this, swipeRefreshLayout::setRefreshing);
        swipeRefreshLayout.setOnRefreshListener(viewModel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                viewModel.onRefresh();
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
        getMenuInflater().inflate(R.menu.menu_sandwhich, menu);
        tintToolbarIcons(menu, R.id.resto_show_website);
        return true;
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.again), v -> viewModel.onRefresh())
                .show();
    }
}