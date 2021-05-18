package be.ugent.zeus.hydra.feed.cards.news;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.news.NewsArticle;

/**
 * Home card for {@link be.ugent.zeus.hydra.news.NewsArticle}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class NewsItemCard extends Card {

    private static final int TWO_WEEKS_HOURS = 14 * 24;

    private final NewsArticle newsItem;

    NewsItemCard(NewsArticle newsItem) {
        this.newsItem = newsItem;
    }

    @Override
    public int getPriority() {
        OffsetDateTime date = getNewsItem().getUpdated();
        Duration duration = Duration.between(date, OffsetDateTime.now());
        return PriorityUtils.lerp((int) duration.toHours(), 0, TWO_WEEKS_HOURS);
    }

    @Override
    public String getIdentifier() {
        return newsItem.getId();
    }

    @Override
    public int getCardType() {
        return Card.Type.NEWS_ITEM;
    }

    @SuppressWarnings("WeakerAccess")
    public NewsArticle getNewsItem() {
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
