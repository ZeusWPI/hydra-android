package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.fragments.common.CachedLoaderFragment;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.recyclerview.adapters.SchamperListAdapter;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.utils.recycler.SpanItemSpacingDecoration;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Display Schamper articles in the main activity.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperFragment extends CachedLoaderFragment<Articles> {

    private SchamperListAdapter adapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schamper, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = $(view, R.id.recycler_view);
        adapter = new SchamperListAdapter();

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    protected SchamperArticlesRequest getRequest() {
        return new SchamperArticlesRequest();
    }

    @Override
    public void receiveData(@NonNull Articles data) {
        adapter.setItems(data);
    }
}