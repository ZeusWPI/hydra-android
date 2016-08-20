package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.fragments.common.ItemAdapterLoaderFragment;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.adapters.SchamperListAdapter;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperFragment extends ItemAdapterLoaderFragment<Article, Articles> {

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
    public SchamperArticlesRequest getRequest() {
        return new SchamperArticlesRequest();
    }
}
