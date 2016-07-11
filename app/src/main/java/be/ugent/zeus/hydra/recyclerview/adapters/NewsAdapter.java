package be.ugent.zeus.hydra.recyclerview.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.recyclerview.viewholder.NewsItemViewHolder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ellen on 8/3/16.
 */
public class NewsAdapter extends ItemAdapter<NewsItem, NewsItemViewHolder> {

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup p, int viewType) {
        return new NewsItemViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_news, p, false));
    }

    @Override
    public void setItems(List<NewsItem> list) {

        Collections.sort(list, new Comparator<NewsItem>() {  // sort the array so that events are added in the right
            @Override
            public int compare(NewsItem lhs, NewsItem rhs) {
                return -lhs.getDate().compareTo(rhs.getDate());
            }
        });

        super.setItems(list);
    }
}