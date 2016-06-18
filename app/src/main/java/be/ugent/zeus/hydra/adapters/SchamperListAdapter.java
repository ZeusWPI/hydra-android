package be.ugent.zeus.hydra.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.SchamperCard;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.recyclerviewholder.home.SchamperViewHolder;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperListAdapter extends RecyclerView.Adapter {
    private List<SchamperCard> articles = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_schamper_card, parent, false);
        SchamperViewHolder vh = new SchamperViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SchamperViewHolder) holder).populate(this.articles.get(position));
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    public void setArticles(Articles articles) {
        this.articles.clear();
        for (Article article: articles) {
            this.articles.add(new SchamperCard(article));
        }
    }
}
