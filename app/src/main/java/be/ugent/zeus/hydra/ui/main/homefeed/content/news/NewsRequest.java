package be.ugent.zeus.hydra.ui.main.homefeed.content.news;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.association.NewsItem;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDateTime;

import java.util.Arrays;

/**
 * @author Niko Strijbol
 */
public class NewsRequest implements HomeFeedRequest {

    private final Request<NewsItem[]> request;

    public NewsRequest(Context context, boolean shouldRefresh) {
        this.request = new CachedRequest<>(context, new be.ugent.zeus.hydra.data.network.requests.NewsRequest(), shouldRefresh);
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.NEWS_ITEM;
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest() throws RequestFailureException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = now.minusMonths(2);

        return StreamSupport.stream(Arrays.asList(request.performRequest()))
                .filter(n -> sixMonthsAgo.isBefore(n.getLocalDate()))
                .map(NewsItemCard::new);
    }
}