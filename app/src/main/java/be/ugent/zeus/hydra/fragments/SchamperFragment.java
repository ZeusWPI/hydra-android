package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.recyclerview.adapters.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.adapters.SchamperListAdapter;
import be.ugent.zeus.hydra.fragments.common.RecyclerLoaderFragment;
import be.ugent.zeus.hydra.loader.cache.Request;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperFragment extends RecyclerLoaderFragment<Article, Articles> {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recycler_view, container, false);
    }

    /**
     * @return The adapter to use.
     */
    @Override
    protected ItemAdapter<Article, ?> getAdapter() {
        return new SchamperListAdapter();
    }

    /**
     * @return The request that will be executed.
     */
    @Override
    public Request<Articles> getRequest() {
        return new SchamperArticlesRequest();
    }
}
