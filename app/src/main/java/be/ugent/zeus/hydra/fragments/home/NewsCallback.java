package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.models.association.News;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.NewsItemCard;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.NewsRequest;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for news items.
 *
 * @author Niko Strijbol
 */
class NewsCallback extends AbstractCallback {

    public NewsCallback(HomeFragment fragment, HomeCardAdapter adapter) {
        super(fragment, adapter);
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.NEWS_ITEM;
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_news;
    }

    @Override
    public Loader<ThrowableEither<List<HomeCard>>> onCreateLoader(int id, Bundle args) {
        return new RequestAsyncTaskLoader<>(new Request(context, fragment.shouldRefresh()), context);
    }

    private class Request extends ProcessableCacheRequest<News, List<HomeCard>> {

        private Request(Context context, boolean shouldRefresh) {
            super(context, new NewsRequest(), shouldRefresh);
        }

        @NonNull
        @Override
        protected List<HomeCard> transform(@NonNull News data) {
            List<HomeCard> newsItemCardList = new ArrayList<>();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sixMonthsAgo = now.minusMonths(6);

            for (NewsItem item : data) {
                if (sixMonthsAgo.isBefore(item.getLocalDate())) {
                    newsItemCardList.add(new NewsItemCard(item));
                }
            }

            return newsItemCardList;
        }
    }


}