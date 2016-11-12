package be.ugent.zeus.hydra.fragments.home.requests;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SchamperCard;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SchamperRequest extends ProcessableCacheRequest<Articles, List<HomeCard>> implements HomeFeedRequest {

    public SchamperRequest(Context context, boolean shouldRefresh) {
        super(context, new SchamperArticlesRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected List<HomeCard> transform(@NonNull Articles data) {
        List<HomeCard> schamperCardList = new ArrayList<>();
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(1);
        for (Article article : data) {
            if (article.getLocalPubDate().isAfter(twoMonthsAgo)) {
                schamperCardList.add(new SchamperCard(article));
            }
        }
        return schamperCardList;
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.SCHAMPER;
    }
}
