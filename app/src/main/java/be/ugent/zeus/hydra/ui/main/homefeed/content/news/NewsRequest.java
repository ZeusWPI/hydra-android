package be.ugent.zeus.hydra.ui.main.homefeed.content.news;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.association.network.UgentNewsRequest;
import be.ugent.zeus.hydra.association.UgentNewsItem;
import be.ugent.zeus.hydra.feed.Card;
import be.ugent.zeus.hydra.feed.CardRepository;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HideableHomeFeedRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDateTime;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class NewsRequest extends HideableHomeFeedRequest {

    private final Request<List<UgentNewsItem>> request;

    public NewsRequest(Context context, CardRepository cardRepository) {
        super(cardRepository);
        this.request = Requests.map(Requests.cache(context, new UgentNewsRequest()), Arrays::asList);
    }

    @Override
    public int getCardType() {
        return Card.Type.NEWS_ITEM;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@Nullable Bundle args) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = now.minusWeeks(2);

        return request.performRequest(args).map(ugentNewsItems -> StreamSupport.stream(ugentNewsItems)
                .filter(n -> sixMonthsAgo.isBefore(n.getLocalCreated()))
                .map(NewsItemCard::new));
    }
}