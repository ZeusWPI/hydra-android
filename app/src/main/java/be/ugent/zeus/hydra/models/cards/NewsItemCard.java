package be.ugent.zeus.hydra.models.cards;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.association.NewsItem;

/**
 * Created by feliciaan on 18/06/16.
 */
public class NewsItemCard extends HomeCard {

    private NewsItem newsItem;

    public NewsItemCard(NewsItem newsItem) {
        this.newsItem = newsItem;
    }

    @Override
    public int getPriority() {
        int mulitplier;
        if (getNewsItem().isHighlighted()) {
            mulitplier = 45;
        } else {
            mulitplier = 75;
        }
        DateTime jodadate = new DateTime(this.getNewsItem().getDate());
        Duration duration = new Duration(new DateTime(), jodadate);
        return (int) (1000 - (duration.getStandardDays()*mulitplier));
    }

    @Override
    public HomeCardAdapter.HomeType getCardType() {
        return HomeCardAdapter.HomeType.NEWSITEM;
    }

    public NewsItem getNewsItem() {
        return newsItem;
    }

    public void setNewsItem(NewsItem newsItem) {
        this.newsItem = newsItem;
    }
}
