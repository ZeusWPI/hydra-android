package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.NewsItemCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.views.NowToolbar;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * This would be very easy if we could just inherit from
 * {@link be.ugent.zeus.hydra.recyclerview.viewholder.NewsItemViewHolder}. That is unfortunately not the case, so we
 * use composition.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class NewsItemViewHolder extends AbstractViewHolder {

    private be.ugent.zeus.hydra.recyclerview.viewholder.NewsItemViewHolder holder;

    private NowToolbar toolbar;

    private HomeCardAdapter adapter;

    public NewsItemViewHolder(View v, HomeCardAdapter adapter) {
        super(v);
        toolbar = $(v, R.id.card_now_toolbar);
        this.adapter = adapter;
        holder = new be.ugent.zeus.hydra.recyclerview.viewholder.NewsItemViewHolder(v);
    }

    public void populate(HomeCard card) {
        if (card.getCardType() != HomeCard.CardType.NEWS_ITEM) {
            return; // TODO: generate error
        }

        NewsItemCard newsItemCard = (NewsItemCard) card;
        final NewsItem newsItem = newsItemCard.getNewsItem();

        holder.populateData(newsItem);

        toolbar.setOnClickListener(adapter.listener(HomeCard.CardType.NEWS_ITEM));
    }
}
