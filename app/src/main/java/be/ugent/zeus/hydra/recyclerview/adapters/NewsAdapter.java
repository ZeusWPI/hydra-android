package be.ugent.zeus.hydra.recyclerview.adapters;

import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.recyclerview.adapters.common.ItemAdapter;
import be.ugent.zeus.hydra.recyclerview.viewholder.NewsItemViewHolder;
import be.ugent.zeus.hydra.utils.ViewUtils;

/**
 * @author Niko Strijbol
 * @author ellen
 */
public class NewsAdapter extends ItemAdapter<NewsItem, NewsItemViewHolder> {

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup p, int viewType) {
        return new NewsItemViewHolder(ViewUtils.inflate(p, R.layout.item_news));
    }
}