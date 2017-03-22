package be.ugent.zeus.hydra.ui.main;

import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.ui.common.recyclerview.ItemAdapter;
import be.ugent.zeus.hydra.utils.ViewUtils;

/**
 * @author Niko Strijbol
 * @author ellen
 */
class NewsAdapter extends ItemAdapter<UgentNewsItem, NewsItemViewHolder> {

    @Override
    public NewsItemViewHolder onCreateViewHolder(ViewGroup p, int viewType) {
        return new NewsItemViewHolder(ViewUtils.inflate(p, R.layout.item_news));
    }
}