package be.ugent.zeus.hydra.feed.cards.news;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.news.UgentNewsArticle;
import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.Card;
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

        UgentNewsArticle newsItem = card.<NewsItemCard>checkCard(Card.Type.NEWS_ITEM).getNewsItem();

        title.setText(newsItem.getTitle());

        String author = newsItem.getCreators().isEmpty() ? "" : newsItem.getCreators().iterator().next();

        CharSequence dateString;
        if (newsItem.getCreated().toLocalDate().isEqual(newsItem.getModified().toLocalDate())) {
            dateString = DateUtils.relativeDateTimeString(newsItem.getCreated(), itemView.getContext());
        } else {
            dateString = itemView.getContext().getString(R.string.article_date_changed,
                    DateUtils.relativeDateTimeString(newsItem.getCreated(), itemView.getContext(), true),
                    DateUtils.relativeDateTimeString(newsItem.getModified(), itemView.getContext(), true)
            );
        }

        String infoText = itemView.getContext().getString(R.string.deprecated_dot_separated,
                dateString,
                author);
        info.setText(infoText);
        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        itemView.setOnClickListener(v -> ArticleViewer.viewArticle(v.getContext(), newsItem, adapter.getCompanion().getHelper()));
    }
}
