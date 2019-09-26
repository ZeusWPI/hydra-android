package be.ugent.zeus.hydra.feed.cards.implementations.news;

import be.ugent.zeus.hydra.association.news.UgentNewsArticle;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import java9.util.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.OffsetDateTime;

/**
 * Home card for {@link UgentNewsArticle}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class NewsItemCard extends Card {

    private static final int TWO_WEEKS_HOURS = 14 * 24;

    private final UgentNewsArticle newsItem;

    NewsItemCard(UgentNewsArticle newsItem) {
        this.newsItem = newsItem;
    }

    @Override
    public int getPriority() {
        //TODO: multiplier for highlight
        OffsetDateTime date = getNewsItem().getModified();
        Duration duration = Duration.between(date, OffsetDateTime.now());
        return PriorityUtils.lerp((int) duration.toHours(), 0, TWO_WEEKS_HOURS);
    }

    @Override
    public String getIdentifier() {
        return newsItem.getIdentifier();
    }

    @Override
    public int getCardType() {
        return Card.Type.NEWS_ITEM;
    }

    @SuppressWarnings("WeakerAccess")
    public UgentNewsArticle getNewsItem() {
        return newsItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsItemCard that = (NewsItemCard) o;
        return Objects.equals(newsItem, that.newsItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newsItem);
    }
}
