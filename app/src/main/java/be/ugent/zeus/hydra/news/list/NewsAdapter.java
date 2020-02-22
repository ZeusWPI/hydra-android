package be.ugent.zeus.hydra.news.list;

import androidx.annotation.NonNull;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.news.UgentNewsArticle;
import be.ugent.zeus.hydra.common.ui.ViewUtils;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.recyclerview.adapters.DiffAdapter;

/**
 * @author Niko Strijbol
 * @author ellen
 */
class NewsAdapter extends DiffAdapter<UgentNewsArticle, NewsItemViewHolder> {

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
