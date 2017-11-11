package be.ugent.zeus.hydra.ui.main.homefeed.content.news;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.domain.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.data.network.requests.association.UgentNewsRequest;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDateTime;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class NewsRequest implements HomeFeedRequest {

    private final Request<List<UgentNewsItem>> request;

    public NewsRequest(Context context) {
        this.request = Requests.map(Requests.cache(context, new UgentNewsRequest()), Arrays::asList);
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.NEWS_ITEM;
    }

    @NonNull
    @Override
    public Result<Stream<HomeCard>> performRequest(Bundle args) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = now.minusWeeks(2);

        return request.performRequest(args).map(ugentNewsItems -> StreamSupport.stream(ugentNewsItems)
                .filter(n -> sixMonthsAgo.isBefore(n.getLocalCreated()))
                .map(NewsItemCard::new));
    }
}