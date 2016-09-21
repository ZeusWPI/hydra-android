package be.ugent.zeus.hydra.fragments.sko;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.view.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.fragments.common.RecyclerLoaderFragment;
import be.ugent.zeus.hydra.models.sko.Exhibitor;
import be.ugent.zeus.hydra.models.sko.Exhibitors;
import be.ugent.zeus.hydra.recyclerview.adapters.sko.ExhibitorAdapter;
import be.ugent.zeus.hydra.requests.sko.StuVilExhibitorRequest;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class VillageFragment extends RecyclerLoaderFragment<Exhibitor, Exhibitors, ExhibitorAdapter> implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout refreshLayout;
    private SearchView searchView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_village, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = $(view, R.id.search_view);
        searchView.setSuggestionsAdapter(null);
        searchView.setOnQueryTextListener(adapter);
        recyclerView.requestFocus();

        refreshLayout = $(view, R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected ExhibitorAdapter getAdapter() {
        return new ExhibitorAdapter();
    }

    @Override
    public void receiveData(@NonNull Exhibitors data) {
        super.receiveData(data);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public CacheRequest<Exhibitors, Exhibitors> getRequest() {
        return new StuVilExhibitorRequest();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sko_refresh, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_refresh) {
            onRefresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        shouldRenew = true;
        searchView.setQuery("", false);
        restartLoader();
        shouldRenew = false;
    }
}