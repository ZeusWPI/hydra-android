package be.ugent.zeus.hydra.feed.cards.implementations.news;

import be.ugent.zeus.hydra.association.news.UgentNewsItem;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.association.news.NewsItem;
import java8.util.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

/**
 * Home card for {@link NewsItem}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class NewsItemCard extends Card {

    private static final int TWO_WEEKS_HOURS = 14 * 24;

    private UgentNewsItem newsItem;

    NewsItemCard(UgentNewsItem newsItem) {
        this.newsItem = newsItem;
    }

    @Override
    public int getPriority() {
        //TODO: multiplier for highlight
        ZonedDateTime date = getNewsItem().getCreated();
        Duration duration = Duration.between(date, ZonedDateTime.now());
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

    public UgentNewsItem getNewsItem() {
        return newsItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsItemCard that = (NewsItemCard) o;
        return java8.util.Objects.equals(newsItem, that.newsItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newsItem);
    }
}