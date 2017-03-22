package be.ugent.zeus.hydra.ui.main.homefeed.content.news;

import android.view.View;

import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HideableViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;

/**
 * Wrapper around another view holder.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class NewsItemViewHolder extends HideableViewHolder {

    private final be.ugent.zeus.hydra.ui.main.NewsItemViewHolder holder;

    public NewsItemViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        holder = new be.ugent.zeus.hydra.ui.main.NewsItemViewHolder(v);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        UgentNewsItem newsItem = card.<NewsItemCard>checkCard(HomeCard.CardType.NEWS_ITEM).getNewsItem();
        holder.populate(newsItem);
    }
}