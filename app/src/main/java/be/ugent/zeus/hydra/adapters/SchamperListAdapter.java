package be.ugent.zeus.hydra.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.recyclerviewholder.SchamperViewHolder;

/**
 * Created by feliciaan on 17/06/16.
 */
public class SchamperListAdapter extends RecyclerView.Adapter {
    private Articles articles = new Articles();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schamper_list_item, parent, false);
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
        this.articles.addAll(articles);
    }
}
