package be.ugent.zeus.hydra.ui.main.homefeed.content.news;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.data.network.requests.association.UgentNewsRequest;
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

    private final Request<UgentNewsItem[]> request;

    public NewsRequest(Context context, boolean shouldRefresh) {
        this.request = new CachedRequest<>(context, new UgentNewsRequest(), shouldRefresh);
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.NEWS_ITEM;
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest(Bundle args) throws RequestFailureException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = now.minusWeeks(2);

        return StreamSupport.stream(Arrays.asList(request.performRequest(null)))
                .filter(n -> sixMonthsAgo.isBefore(n.getLocalCreated()))
                .map(NewsItemCard::new);
    }
}