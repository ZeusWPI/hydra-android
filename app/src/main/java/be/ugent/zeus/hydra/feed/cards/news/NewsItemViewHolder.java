package be.ugent.zeus.hydra.feed.cards.news;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardViewHolder;
import be.ugent.zeus.hydra.news.NewsArticle;

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

        NewsArticle newsItem = card.<NewsItemCard>checkCard(Card.Type.NEWS_ITEM).getNewsItem();

        title.setText(newsItem.getTitle());
        
        CharSequence dateString;
        if (newsItem.getPublished().toLocalDate().isEqual(newsItem.getUpdated().toLocalDate())) {
            dateString = DateUtils.relativeDateTimeString(newsItem.getPublished(), itemView.getContext());
        } else {
            dateString = itemView.getContext().getString(R.string.article_date_changed,
                    DateUtils.relativeDateTimeString(newsItem.getPublished(), itemView.getContext(), true),
                    DateUtils.relativeDateTimeString(newsItem.getUpdated(), itemView.getContext(), true)
            );
        }
        
        info.setText(dateString);
        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        itemView.setOnClickListener(v -> ArticleViewer.viewArticle(v.getContext(), newsItem, adapter.getCompanion().getHelper()));
    }
}
