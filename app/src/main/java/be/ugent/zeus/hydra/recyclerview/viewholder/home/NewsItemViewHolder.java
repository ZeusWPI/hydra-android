package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.view.View;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.NewsItemCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;

/**
 * Wrapper around another view holder.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class NewsItemViewHolder extends HideableViewHolder {

    private be.ugent.zeus.hydra.recyclerview.viewholder.NewsItemViewHolder holder;

    public NewsItemViewHolder(View v, HomeCardAdapter adapter) {
        super(v, adapter);
        holder = new be.ugent.zeus.hydra.recyclerview.viewholder.NewsItemViewHolder(v);
    }

    @Override
    public void populate(HomeCard card) {

        final NewsItem newsItem = card.<NewsItemCard>checkCard(HomeCard.CardType.NEWS_ITEM).getNewsItem();
        holder.populate(newsItem);

        super.populate(card);
    }
}