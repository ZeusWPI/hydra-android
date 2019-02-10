package be.ugent.zeus.hydra.feed.cards.implementations.news;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.association.news.UgentNewsArticle;
import be.ugent.zeus.hydra.association.news.UgentNewsRequest;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardRepository;
import java9.util.stream.Stream;
import java9.util.stream.StreamSupport;
import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class NewsRequest extends HideableHomeFeedRequest {

    private final Request<List<UgentNewsArticle>> request;

    public NewsRequest(Context context, CardRepository cardRepository) {
        super(cardRepository);
        this.request = new UgentNewsRequest(context);
    }

    @Override
    public int getCardType() {
        return Card.Type.NEWS_ITEM;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@NonNull Bundle args) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = now.minusWeeks(2);

        return request.execute(args).map(ugentNewsItems -> StreamSupport.stream(ugentNewsItems)
                .filter(ugentNewsItem -> sixMonthsAgo.isBefore(ugentNewsItem.getLocalModified()))
                .map(NewsItemCard::new));
    }
}