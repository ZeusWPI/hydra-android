package be.ugent.zeus.hydra.feed.cards.schamper;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;
import be.ugent.zeus.hydra.schamper.Article;
import be.ugent.zeus.hydra.schamper.SchamperArticlesRequest;

/**
 * @author Niko Strijbol
 */
public class SchamperRequest extends HideableHomeFeedRequest {

    private final Request<List<Article>> request;

    public SchamperRequest(Context context, DismissalDao dismissalDao) {
        super(dismissalDao);
        this.request = new SchamperArticlesRequest(context);
    }

    @Override
    public int getCardType() {
        return Card.Type.SCHAMPER;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@NonNull Bundle args) {
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(1);

        return request.execute(args).map(articles -> articles.stream()
                .filter(a -> a.getLocalPubDate().isAfter(twoMonthsAgo))
                .map(SchamperCard::new));
    }
}
