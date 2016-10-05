package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.SchamperCard;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.models.schamper.Articles;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for Schamper articles.
 *
 * @author Niko Strijbol
 */
class SchamperCallback extends AbstractCallback {

    public SchamperCallback(HomeFragment fragment, HomeCardAdapter adapter) {
        super(fragment, adapter);
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.SCHAMPER;
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_schamper;
    }

    @Override
    public Loader<ThrowableEither<List<HomeCard>>> onCreateLoader(int id, Bundle args) {
        return new RequestAsyncTaskLoader<>(new Request(context, fragment.shouldRefresh()), context);
    }

    private static class Request extends ProcessableCacheRequest<Articles, List<HomeCard>> {

        public Request(Context context, boolean shouldRefresh) {
            super(context, new SchamperArticlesRequest(), shouldRefresh);
        }

        @NonNull
        @Override
        protected List<HomeCard> transform(@NonNull Articles data) {
            List<HomeCard> schamperCardList = new ArrayList<>();
            for (Article article : data) {
                schamperCardList.add(new SchamperCard(article));
            }
            return schamperCardList;
        }
    }
}