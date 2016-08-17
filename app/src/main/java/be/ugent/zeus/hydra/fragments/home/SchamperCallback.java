package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SchamperCard;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for Schamper articles.
 *
 * @author Niko Strijbol
 */
class SchamperCallback extends HomeLoaderCallback<Articles> {

    public SchamperCallback(Context context, HomeCardAdapter adapter, FragmentCallback callback) {
        super(context, adapter, callback);
    }

    @Override
    protected List<HomeCard> convertData(@NonNull Articles data) {
        List<HomeCard> schamperCardList = new ArrayList<>();
        for (Article article : data) {
            schamperCardList.add(new SchamperCard(article));
        }
        return schamperCardList;
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.SCHAMPER;
    }

    @Override
    protected SchamperArticlesRequest getCacheRequest() {
        return new SchamperArticlesRequest();
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_schamper;
    }
}