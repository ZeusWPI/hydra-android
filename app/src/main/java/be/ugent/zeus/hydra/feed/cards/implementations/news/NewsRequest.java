package be.ugent.zeus.hydra.feed.cards.implementations.news;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.association.news.UgentNewsRequest;
import be.ugent.zeus.hydra.association.news.UgentNewsItem;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardRepository;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
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