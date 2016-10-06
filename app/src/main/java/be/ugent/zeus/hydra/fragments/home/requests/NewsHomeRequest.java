package be.ugent.zeus.hydra.fragments.home.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.association.News;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.NewsItemCard;
import be.ugent.zeus.hydra.requests.NewsRequest;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class NewsHomeRequest extends ProcessableCacheRequest<News, List<HomeCard>> implements HomeFeedRequest {

    public NewsHomeRequest(Context context, boolean shouldRefresh) {
        super(context, new NewsRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected List<HomeCard> transform(@NonNull News data) {
        List<HomeCard> newsItemCardList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = now.minusMonths(2);

        for (NewsItem item : data) {
            if (sixMonthsAgo.isBefore(item.getLocalDate())) {
                newsItemCardList.add(new NewsItemCard(item));
            }
        }

        return newsItemCardList;
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.NEWS_ITEM;
    }
}
