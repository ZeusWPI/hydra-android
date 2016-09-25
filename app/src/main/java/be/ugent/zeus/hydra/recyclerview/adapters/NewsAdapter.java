package be.ugent.zeus.hydra.recyclerview.adapters;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.recyclerview.adapters.common.Adapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.NewsItemViewHolder;

import java.util.List;

/**
 * Created by ellen on 8/3/16.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsItemViewHolder> implements Adapter<NewsItem> {

    private SortedList<NewsItem> data = new SortedList<>(NewsItem.class, new SortedListAdapterCallback<NewsItem>(this) {

        @Override
        public int compare(NewsItem o1, NewsItem o2) {
            return -o1.getDate().compareTo(o2.getDate());
        }

        @Override
        public boolean areContentsTheSame(NewsItem oldItem, NewsItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areItemsTheSame(NewsItem item1, NewsItem item2) {
            return item1 == item2;
        }
    });

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup p, int viewType) {
        return new NewsItemViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_news, p, false));
    }

    @Override
    public void onBindViewHolder(NewsItemViewHolder holder, int position) {
        holder.populate(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void setItems(List<NewsItem> list) {
        data.clear();
        data.addAll(list);
    }
}