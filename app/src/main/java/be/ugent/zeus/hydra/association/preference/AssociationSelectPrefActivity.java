package be.ugent.zeus.hydra.association.preference;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import java.util.*;

/**
 * Allow the user to select preferences.
 *
 * @author Niko Strijbol
 */
public class AssociationSelectPrefActivity extends BaseActivity {

    /**
     * Key for the preference that contains which associations should be shown.
     */
    public static final String PREF_ASSOCIATIONS_SHOWING = "pref_associations_showing";

    private static final String TAG = "AssociationSelectPrefAc";

    private final SearchableAssociationsAdapter adapter = new SearchableAssociationsAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_associations);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerFastScroller s = findViewById(R.id.fast_scroller);
        SearchView searchView = findViewById(R.id.search_view);

        recyclerView.requestFocus();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        s.attachRecyclerView(recyclerView);

        searchView.setOnQueryTextListener(adapter);

        AssociationsViewModel model = ViewModelProviders.of(this).get(AssociationsViewModel.class);
        model.getData().observe(this, PartialErrorObserver.with(this::onError));
        model.getData().observe(this, SuccessObserver.with(this::receiveData));
        model.getData().observe(this, new ProgressObserver<>(findViewById(R.id.progress_bar)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pref_selectors, menu);
        tintToolbarIcons(menu, R.id.action_select_all, R.id.action_select_none);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_select_all:
                adapter.setAllChecked(true);
                return true;
            case R.id.action_select_none:
                adapter.setAllChecked(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void receiveData(@NonNull List<Association> data) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> disabled = preferences.getStringSet(PREF_ASSOCIATIONS_SHOWING, Collections.emptySet());
        List<Pair<Association, Boolean>> values = new ArrayList<>();

        for (Association association : data) {
            values.add(new Pair<>(association, !disabled.contains(association.getInternalName())));
        }

        adapter.submitData(values);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Save the values.
        Set<String> disabled = new HashSet<>();
        for (Pair<Association, Boolean> pair : adapter.getItemsAndState()) {
            if (!pair.second) {
                disabled.add(pair.first.getInternalName());
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putStringSet(PREF_ASSOCIATIONS_SHOWING, disabled).apply();
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.failure), Snackbar.LENGTH_LONG).show();
    }
}