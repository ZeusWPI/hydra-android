package be.ugent.zeus.hydra.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.recyclerviewholder.home.SchamperViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperListAdapter extends RecyclerView.Adapter<SchamperViewHolder> {

    private List<Article> articles = new ArrayList<>();

    @Override
    public SchamperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_schamper, parent, false);
        return new SchamperViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SchamperViewHolder holder, int position) {
        holder.populate(this.articles.get(position));
    }

    @Override
    public int getItemCount() {
        return this.articles.size();
    }

    public void setArticles(Articles articles) {
        this.articles.clear();
        this.articles.addAll(articles);
        notifyDataSetChanged();
    }
}
