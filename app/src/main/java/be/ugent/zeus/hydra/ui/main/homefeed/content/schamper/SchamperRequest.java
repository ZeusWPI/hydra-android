package be.ugent.zeus.hydra.ui.main.homefeed.content.schamper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.schamper.Article;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.ListRequest;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.data.network.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SchamperRequest implements HomeFeedRequest {

    private final Request<List<Article>> request;

    public SchamperRequest(Context context, boolean shouldRefresh) {
        this.request = new ListRequest<>(new CachedRequest<>(context, new SchamperArticlesRequest(), shouldRefresh));
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.SCHAMPER;
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest(Bundle args) throws RequestFailureException {
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(1);

        return StreamSupport.stream(request.performRequest(null))
                .filter(a -> a.getLocalPubDate().isAfter(twoMonthsAgo))
                .map(SchamperCard::new);
    }
}