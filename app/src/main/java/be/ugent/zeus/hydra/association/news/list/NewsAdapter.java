package be.ugent.zeus.hydra.association.news.list;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.news.UgentNewsItem;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.ItemAdapter2;

/**
 * @author Niko Strijbol
 * @author ellen
 */
class NewsAdapter extends ItemAdapter2<UgentNewsItem, NewsItemViewHolder> {

    private final ActivityHelper helper;

    NewsAdapter(ActivityHelper helper) {
        super();
        this.helper = helper;
    }

    @NonNull
    @Override
    public NewsItemViewHolder onCreateViewHolder(@NonNull ViewGroup p, int viewType) {
        return new NewsItemViewHolder(ViewUtils.inflate(p, R.layout.item_news), helper);
    }
}