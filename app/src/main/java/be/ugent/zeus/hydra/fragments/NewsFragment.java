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
import be.ugent.zeus.hydra.models.association.News;
import be.ugent.zeus.hydra.recyclerview.adapters.NewsAdapter;
import be.ugent.zeus.hydra.requests.NewsRequest;
import be.ugent.zeus.hydra.utils.recycler.SpanItemSpacingDecoration;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Created by Ellen on 07/04/2016.
 */
public class NewsFragment extends CachedLoaderFragment<News> {

    private NewsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new NewsAdapter();

        RecyclerView recyclerView = $(view, R.id.recycler_view);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpanItemSpacingDecoration(getContext()));
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    protected NewsRequest getRequest() {
        return new NewsRequest();
    }

    @Override
    public void receiveData(@NonNull News data) {
        adapter.setItems(data);
    }
}
