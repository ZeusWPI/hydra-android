package be.ugent.zeus.hydra.fragments.sko;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.fragments.common.CachedLoaderFragment;
import be.ugent.zeus.hydra.models.sko.Exhibitors;
import be.ugent.zeus.hydra.recyclerview.adapters.sko.ExhibitorAdapter;
import be.ugent.zeus.hydra.requests.sko.StuVilExhibitorRequest;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class VillageFragment extends CachedLoaderFragment<Exhibitors> {

    private ExhibitorAdapter adapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_village, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = $(view, R.id.recycler_view);
        searchView = $(view, R.id.search_view);
        adapter = new ExhibitorAdapter();
        searchView.setSuggestionsAdapter(null);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void receiveData(@NonNull Exhibitors data) {
        adapter.setItems(data);
    }

    @Override
    public CacheRequest<Exhibitors, Exhibitors> getRequest() {
        return new StuVilExhibitorRequest();
    }

    @Override
    public void onResume() {
        super.onResume();
        searchView.setOnQueryTextListener(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        searchView.setOnQueryTextListener(null);
    }
}