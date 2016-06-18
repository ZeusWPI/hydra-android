package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.association.AssociationNewsItem;

/**
 * Created by feliciaan on 18/06/16.
 */
public class NewsItemCard extends HomeCard {

    private AssociationNewsItem newsItem;

    public NewsItemCard(AssociationNewsItem newsItem) {
        this.newsItem = newsItem;
    }

    @Override
    public int getPriority() {
        if (getNewsItem().highlighted) {
            return 950; //TODO: rewrite
        } else {
            return 500;
        }
    }

    @Override
    public HomeCardAdapter.HomeType getCardType() {
        return HomeCardAdapter.HomeType.NEWSITEM;
    }

    public AssociationNewsItem getNewsItem() {
        return newsItem;
    }

    public void setNewsItem(AssociationNewsItem newsItem) {
        this.newsItem = newsItem;
    }
}
