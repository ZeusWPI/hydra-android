package be.ugent.zeus.hydra.homefeed.content.news;

import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.homefeed.content.FeedUtils;
import be.ugent.zeus.hydra.models.association.NewsItem;
import java8.util.Objects;
import org.threeten.bp.Duration;
import org.threeten.bp.ZonedDateTime;

/**
 * Home card for {@link NewsItem}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class NewsItemCard extends HomeCard {

    private NewsItem newsItem;

    public NewsItemCard(NewsItem newsItem) {
        this.newsItem = newsItem;
    }

    @Override
    public int getPriority() {
        //TODO: multiplier for highlight
        ZonedDateTime date = getNewsItem().getDate();
        Duration duration = Duration.between(ZonedDateTime.now(), date);
        return FeedUtils.lerp((int) duration.toDays(), 0, 62);
    }

    @Override
    public int getCardType() {
        return CardType.NEWS_ITEM;
    }

    public NewsItem getNewsItem() {
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