package be.ugent.zeus.hydra.activities.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.models.association.Association;
import be.ugent.zeus.hydra.models.association.Associations;
import be.ugent.zeus.hydra.plugins.RequestPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.recyclerview.adapters.MultiSelectListAdapter;
import be.ugent.zeus.hydra.requests.association.AssociationsRequest;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.*;

/**
 * Allow the user to select preferences.
 *
 * @author Niko Strijbol
 */
public class AssociationSelectPrefActivity extends HydraActivity {

    public static final String PREF_ASSOCIATIONS_SHOWING = "pref_associations_showing";

    private SearchableAdapter adapter = new SearchableAdapter();
    private RequestPlugin<Associations> plugin = RequestPlugin.cached(new AssociationsRequest());

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.hasProgress()
                .defaultError()
                .setDataCallback(this::receiveData);
        plugins.add(plugin);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_associations);

        final RecyclerView recyclerView = $(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        FastScroller scroller = $(R.id.fast_scroller);
        SearchView searchView = $(R.id.search_view);

        recyclerView.requestFocus();

        adapter = new SearchableAdapter();
        adapter.setDisplayNameProvider(Association::getName);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //TODO nicer bubble
        scroller.setRecyclerView(recyclerView);

        searchView.setOnQueryTextListener(adapter);
        plugin.startLoader();
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

    private void receiveData(@NonNull Associations data) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> disabled = preferences.getStringSet(PREF_ASSOCIATIONS_SHOWING, Collections.emptySet());
        List<Pair<Association, Boolean>> values = new ArrayList<>();

        for (Association association : data) {
            values.add(new Pair<>(association, !disabled.contains(association.getInternalName())));
        }

        adapter.setItems(values);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Save the values.
        Set<String> disabled = new HashSet<>();
        for (Map.Entry<Association, Boolean> pair : adapter.allData.entrySet()) {
            if (!pair.getValue()) {
                disabled.add(pair.getKey().getInternalName());
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putStringSet(PREF_ASSOCIATIONS_SHOWING, disabled).apply();
    }

    private static class SearchableAdapter extends MultiSelectListAdapter<Association> implements SearchView.OnQueryTextListener, SectionTitleProvider {

        private Map<Association, Boolean> allData = new HashMap<>();

        @Override
        public void setItems(List<Pair<Association, Boolean>> items) {
            super.setItems(items);
            for (Pair<Association, Boolean> pair : items) {
                allData.put(pair.first, pair.second);
            }
        }

        @Override
        public void setChecked(int position) {
            super.setChecked(position);
            Pair<Association, Boolean> newData = items.get(position);
            allData.put(newData.first, newData.second);
        }

        @Override
        public void setAllChecked(boolean checked) {
            super.setAllChecked(checked);
            for (Association key : allData.keySet()) {
                allData.put(key, checked);
            }
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {

            if (allData == null) {
                return true;
            }

            if (newText.isEmpty()) {
                List<Pair<Association, Boolean>> newList = new ArrayList<>();
                for (Map.Entry<Association, Boolean> pair : allData.entrySet()) {
                    newList.add(new Pair<>(pair.getKey(), pair.getValue()));
                }
                this.items = newList;
                notifyDataSetChanged();
            }

            List<Pair<Association, Boolean>> newList = new ArrayList<>();

            for (Map.Entry<Association, Boolean> pair : allData.entrySet()) {
                String text = newText.toLowerCase();
                Association a = pair.getKey();
                if (a.getDisplayName().toLowerCase().contains(text) ||
                        (a.getFullName() != null && a.getFullName().toLowerCase().contains(text)) ||
                        a.getInternalName().toLowerCase().contains(text)) {
                    newList.add(new Pair<>(a, pair.getValue()));
                }
            }

            //Manually update.
            this.items = newList;
            notifyDataSetChanged();

            return true;
        }

        @Override
        public String getSectionTitle(int position) {
            return items.get(position).first.getParentAssociation();
        }
    }
}