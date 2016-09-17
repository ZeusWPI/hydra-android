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
import be.ugent.zeus.hydra.activities.common.LoaderToolbarActivity;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.models.association.Association;
import be.ugent.zeus.hydra.models.association.Associations;
import be.ugent.zeus.hydra.recyclerview.adapters.MultiSelectListAdapter;
import be.ugent.zeus.hydra.requests.AssociationsRequest;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;

import java.util.*;

/**
 * Allow the user to select preferences.
 *
 * @author Niko Strijbol
 */
public class AssociationSelectPrefActivity extends LoaderToolbarActivity<Associations> {

    public static final String PREF_ASSOCIATIONS_SHOWING = "pref_associations_showing";

    private SearchableAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences_associations);

        final RecyclerView recyclerView = $(R.id.recycler_view);
        FastScroller scroller = $(R.id.fast_scroller);
        SearchView searchView = $(R.id.search_view);

        recyclerView.requestFocus();

        adapter = new SearchableAdapter();
        adapter.setDisplayNameProvider(new MultiSelectListAdapter.DisplayNameProvider<Association>() {
            @Override
            public String getDisplayValue(Association element) {
                return element.getName();
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //TODO nicer bubble
        scroller.setRecyclerView(recyclerView);

        searchView.setOnQueryTextListener(adapter);
        startLoader();
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

    @Override
    public void receiveData(@NonNull Associations data) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> disabled = preferences.getStringSet(PREF_ASSOCIATIONS_SHOWING, Collections.<String>emptySet());
        List<Pair<Association, Boolean>> values = new ArrayList<>();

        for(Association association: data) {
            values.add(new Pair<>(association, !disabled.contains(association.getInternalName())));
        }

        adapter.setItems(values);
    }

    @Override
    public CacheRequest<Associations, Associations> getRequest() {
        return new AssociationsRequest();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Save the values.
        Set<String> disabled = new HashSet<>();
        for (Pair<Association, Boolean> pair: adapter.getItems()) {
            if(!pair.second) {
                disabled.add(pair.first.getInternalName());
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putStringSet(PREF_ASSOCIATIONS_SHOWING, disabled).apply();
    }

    private static class SearchableAdapter extends MultiSelectListAdapter<Association> implements SearchView.OnQueryTextListener, SectionTitleProvider {

        private List<Pair<Association, Boolean>> allData;

        @Override
        public void setItems(List<Pair<Association, Boolean>> list) {
            super.setItems(list);
            allData = list;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {

            if(allData == null) {
                return true;
            }

            if(newText.isEmpty()) {
                this.items = allData;
                notifyDataSetChanged();
            }

            List<Pair<Association, Boolean>> newList = new ArrayList<>();

            for(Pair<Association, Boolean> pair: allData) {
                Association a = pair.first;
                String text = newText.toLowerCase();
                if(a.getDisplayName().toLowerCase().contains(text) || (a.getFullName() != null && a.getFullName().toLowerCase().contains(text))) {
                    newList.add(pair);
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