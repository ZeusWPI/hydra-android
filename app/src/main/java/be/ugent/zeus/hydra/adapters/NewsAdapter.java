package be.ugent.zeus.hydra.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.News;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.models.cards.NewsItemCard;
import be.ugent.zeus.hydra.recyclerviewholder.home.NewsItemViewHolder;

/**
 * Created by ellen on 8/3/16.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsItemViewHolder> {
    private List<NewsItemCard> items = new ArrayList<>();

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_news_item_card, parent, false);
        NewsItemViewHolder vh = new NewsItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(NewsItemViewHolder holder, int position) {
        final NewsItemCard newsItemCard = items.get(position);
        holder.populate(newsItemCard);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(News items) {
        this.items.clear();
        for (NewsItem item : items) { //TODO: remove everything older than 6-months
            this.items.add(new NewsItemCard(item));
        }

        Collections.sort(this.items, new Comparator<NewsItemCard>() {  // sort the array so that events are added in the right
            @Override
            public int compare(NewsItemCard lhs, NewsItemCard rhs) {
                return -lhs.getNewsItem().getDate().compareTo(rhs.getNewsItem().getDate());
            }
        });
    }
}