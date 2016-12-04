package be.ugent.zeus.hydra.homefeed.content.schamper;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;
import org.threeten.bp.LocalDateTime;

/**
 * @author Niko Strijbol
 */
public class SchamperRequest extends ProcessableCacheRequest<Articles, Stream<HomeCard>> implements HomeFeedRequest {

    public SchamperRequest(Context context, boolean shouldRefresh) {
        super(context, new SchamperArticlesRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected Stream<HomeCard> transform(@NonNull Articles data) {
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(1);

        return StreamSupport.stream(data)
                .filter(a -> a.getLocalPubDate().isAfter(twoMonthsAgo))
                .map(SchamperCard::new);
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.SCHAMPER;
    }
}
