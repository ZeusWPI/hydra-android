package be.ugent.zeus.hydra.models.cards;

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
        if (getNewsItem().isHighlighted()) {
            return 950; //TODO: rewrite
        } else {
            return 500;
        }
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
