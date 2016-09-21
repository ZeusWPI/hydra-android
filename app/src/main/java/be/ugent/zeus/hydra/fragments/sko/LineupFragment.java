package be.ugent.zeus.hydra.fragments.sko;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.fragments.common.CachedLoaderFragment;
import be.ugent.zeus.hydra.models.sko.Artist;
import be.ugent.zeus.hydra.models.sko.Artists;
import be.ugent.zeus.hydra.recyclerview.adapters.sko.LineupAdapter;
import be.ugent.zeus.hydra.requests.sko.LineupRequest;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Show the lineup.
 *
 * @author Niko Strijbol
 */
public class LineupFragment extends CachedLoaderFragment<Artists> implements SwipeRefreshLayout.OnRefreshListener {

    private RvJoiner joiner;
    private Map<String, LineupAdapter> adapters = new HashMap<>();
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

        RecyclerView recyclerView = $(view, R.id.recycler_view);

        refreshLayout = $(view, R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(this);

        joiner = new RvJoiner();
        recyclerView.setAdapter(joiner.getAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void receiveData(@NonNull Artists data) {
        //Sort into stages
        Map<String, List<Artist>> stages = new HashMap<>();

        for (Artist artist: data) {
            if(!stages.containsKey(artist.getStage())) {
                stages.put(artist.getStage(), new ArrayList<Artist>());
            }
            stages.get(artist.getStage()).add(artist);
        }

        for (Map.Entry<String, List<Artist>> entry: stages.entrySet()) {
            if(!adapters.containsKey(entry.getKey())) {
                LineupAdapter adapter = new LineupAdapter();
                adapters.put(entry.getKey(), adapter);
                joiner.add(new JoinableLayout(R.layout.item_title, new Callback(entry.getKey())));
                joiner.add(new JoinableAdapter(adapter));
            }
            adapters.get(entry.getKey()).setItems(entry.getValue());
        }

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
    public LineupRequest getRequest() {
        return new LineupRequest();
    }

    /**
     * Callback for the header.
     */
    private static class Callback implements JoinableLayout.Callback {

        private String text;

        public Callback(String text) {
            this.text = text;
        }

        public Callback(@StringRes int id, Context context) {
            text = context.getString(id);
        }

        @Override
        public void onInflateComplete(View view, ViewGroup parent) {
            TextView v = $(view, R.id.text_header);
            v.setText(text);
        }
    }
}