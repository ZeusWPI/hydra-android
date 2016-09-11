package be.ugent.zeus.hydra.fragments.sko;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.fragments.common.CachedLoaderFragment;
import be.ugent.zeus.hydra.models.sko.Stage;
import be.ugent.zeus.hydra.models.sko.Stages;
import be.ugent.zeus.hydra.recyclerview.adapters.sko.LineupAdapter;
import be.ugent.zeus.hydra.requests.sko.LineupRequest;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Show the lineup.
 *
 * @author Niko Strijbol
 */
public class LineupFragment extends CachedLoaderFragment<Stages> implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private LineupAdapter adapter;
    private SwipeRefreshLayout refreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_lineup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = $(view, R.id.recycler_view);
        refreshLayout = $(view, R.id.refresh_layout);

        refreshLayout.setOnRefreshListener(this);

        adapter = new LineupAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void receiveData(@NonNull Stages data) {

        //Get first stage
        Stage stage = data.get(0);

        adapter.setItems(stage.getArtists());
        
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        shouldRenew = true;
        restartLoader();
        shouldRenew = false;
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
    public CacheRequest<Stages, Stages> getRequest() {
        return new LineupRequest();
    }
}