package be.ugent.zeus.hydra.homefeed.content.news;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.data.models.association.News;
import be.ugent.zeus.hydra.data.network.ProcessableCacheRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDateTime;

/**
 * @author Niko Strijbol
 */
public class NewsRequest extends ProcessableCacheRequest<News, Stream<HomeCard>> implements HomeFeedRequest {

    public NewsRequest(Context context, boolean shouldRefresh) {
        super(context, new be.ugent.zeus.hydra.data.network.requests.NewsRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected Stream<HomeCard> transform(@NonNull News data) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = now.minusMonths(2);

        return StreamSupport.stream(data)
                .filter(n -> sixMonthsAgo.isBefore(n.getLocalDate()))
                .map(NewsItemCard::new);
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.NEWS_ITEM;
    }
}