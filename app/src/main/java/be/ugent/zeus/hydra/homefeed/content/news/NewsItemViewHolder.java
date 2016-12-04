package be.ugent.zeus.hydra.homefeed.content.news;

import android.view.View;
import be.ugent.zeus.hydra.homefeed.content.HideableViewHolder;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;

/**
 * Wrapper around another view holder.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class NewsItemViewHolder extends HideableViewHolder {

    private final be.ugent.zeus.hydra.recyclerview.viewholder.NewsItemViewHolder holder;

    public NewsItemViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        holder = new be.ugent.zeus.hydra.recyclerview.viewholder.NewsItemViewHolder(v);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        NewsItem newsItem = card.<NewsItemCard>checkCard(HomeCard.CardType.NEWS_ITEM).getNewsItem();
        holder.populate(newsItem);
    }
}