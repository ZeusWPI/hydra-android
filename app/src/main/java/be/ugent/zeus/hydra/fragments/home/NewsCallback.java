package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.association.News;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.NewsItemCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.NewsRequest;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for news items.
 *
 * @author Niko Strijbol
 */
class NewsCallback extends HomeLoaderCallback<News> {

    public NewsCallback(Context context, HomeCardAdapter adapter, FragmentCallback callback) {
        super(context, adapter, callback);
    }

    @Override
    protected List<HomeCard> convertData(@NonNull News data) {
        List<HomeCard> newsItemCardList = new ArrayList<>();
        DateTime now = DateTime.now();
        DateTime sixMonthsAgo = now.minusMonths(6);

        for (NewsItem item : data) {
            if (sixMonthsAgo.isBefore(item.getDate().getTime())) {
                newsItemCardList.add(new NewsItemCard(item));
            }
        }

        return newsItemCardList;
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.NEWS_ITEM;
    }

    @Override
    protected NewsRequest getCacheRequest() {
        return new NewsRequest();
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_news;
    }
}