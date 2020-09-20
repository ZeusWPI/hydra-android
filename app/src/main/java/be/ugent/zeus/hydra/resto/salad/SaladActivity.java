package be.ugent.zeus.hydra.resto.salad;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.lifecycle.ViewModelProvider;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.databinding.ActivityRestoSaladBinding;
import com.google.android.material.snackbar.Snackbar;

/**
 * Activity for the sandwiches.
 */
public class SaladActivity extends BaseActivity<ActivityRestoSaladBinding> {

    private static final String TAG = "SaladActivity";

    public static final String URL = "https://www.ugent.be/student/nl/meer-dan-studeren/resto/broodjes";

    private final SaladAdapter adapter = new SaladAdapter();
    private SaladViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityRestoSaladBinding::inflate);

        final ViewModelProvider provider = new ViewModelProvider(this);
        viewModel = provider.get(SaladViewModel.class);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);

        binding.swipeRefreshLayout.setColorSchemeColors(ColourUtils.resolveColour(this, R.attr.colorSecondary));

        viewModel.getData().observe(this, new AdapterObserver<>(adapter));
        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getRefreshing().observe(this, binding.swipeRefreshLayout::setRefreshing);
        viewModel.getData().observe(this, new ProgressObserver<>(binding.progressBar));
        binding.swipeRefreshLayout.setOnRefreshListener(viewModel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.resto_show_website) {
            NetworkUtils.maybeLaunchBrowser(this, URL);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sandwhich, menu);
        tintToolbarIcons(menu, R.id.resto_show_website);
        return true;
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.onRefresh())
                .show();
    }
}
