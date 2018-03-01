package be.ugent.zeus.hydra.feed.cards.implementations.schamper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.CardRepository;
import be.ugent.zeus.hydra.schamper.Article;
import be.ugent.zeus.hydra.schamper.list.SchamperArticlesRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SchamperRequest extends HideableHomeFeedRequest {

    private final Request<List<Article>> request;

    public SchamperRequest(Context context, CardRepository cardRepository) {
        super(cardRepository);
        this.request = Requests.cachedList(context, new SchamperArticlesRequest());
    }

    @Override
    public int getCardType() {
        return Card.Type.SCHAMPER;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@Nullable Bundle args) {
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(1);

        return request.performRequest(args).map(articles -> StreamSupport.stream(articles)
                .filter(a -> a.getLocalPubDate().isAfter(twoMonthsAgo))
                .map(SchamperCard::new));
    }
}