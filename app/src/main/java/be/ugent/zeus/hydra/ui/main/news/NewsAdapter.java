package be.ugent.zeus.hydra.ui.main.news;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.domain.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.recyclerview.adapters.ItemDiffAdapter;

/**
 * @author Niko Strijbol
 * @author ellen
 */
class NewsAdapter extends ItemDiffAdapter<UgentNewsItem, NewsItemViewHolder> {

    private final ActivityHelper helper;

    protected NewsAdapter(ActivityHelper helper) {
        super();
        this.helper = helper;
    }

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup p, int viewType) {
        return new NewsItemViewHolder(ViewUtils.inflate(p, R.layout.item_news), helper);
    }
}