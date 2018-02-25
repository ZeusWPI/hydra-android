package be.ugent.zeus.hydra.association.news.list;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.news.UgentNewsItem;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemDiffAdapter;

/**
 * @author Niko Strijbol
 * @author ellen
 */
class NewsAdapter extends ItemDiffAdapter<UgentNewsItem, NewsItemViewHolder> {

    private final ActivityHelper helper;

    NewsAdapter(ActivityHelper helper) {
        super();
        this.helper = helper;
    }

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup p, int viewType) {
        return new NewsItemViewHolder(ViewUtils.inflate(p, R.layout.item_news), helper);
    }
}