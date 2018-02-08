package be.ugent.zeus.hydra.feed.cards.implementations.news;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.news.UgentNewsItem;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.association.news.NewsArticleActivity;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * View holder for the news card in the home feed.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class NewsItemViewHolder extends CardViewHolder {

    private final TextView info;
    private final TextView title;

    public NewsItemViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = v.findViewById(R.id.name);
        info = v.findViewById(R.id.info);
    }

    @Override
    public void populate(final Card card) {
        super.populate(card);

        UgentNewsItem newsItem = card.<NewsItemCard>checkCard(Card.Type.NEWS_ITEM).getNewsItem();

        title.setText(newsItem.getTitle());

        String author = newsItem.getCreators().isEmpty() ? "" : newsItem.getCreators().iterator().next();

        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                DateUtils.relativeDateTimeString(newsItem.getCreated(), itemView.getContext()),
                author);
        info.setText(infoText);
        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        itemView.setOnClickListener(v -> NewsArticleActivity.viewArticle(v.getContext(), newsItem, adapter.getCompanion().getHelper()));
    }
}