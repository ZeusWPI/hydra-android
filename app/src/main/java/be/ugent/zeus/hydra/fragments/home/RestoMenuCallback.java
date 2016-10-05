package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.loaders.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.loaders.ThrowableEither;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.RestoMenuCard;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import be.ugent.zeus.hydra.requests.resto.RestoMenuOverviewRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for the cards containing resto menus.
 *
 * @author Niko Strijbol
 */
class RestoMenuCallback extends AbstractCallback {

    public RestoMenuCallback(HomeFragment fragment, HomeCardAdapter adapter) {
        super(fragment, adapter);
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.RESTO;
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_menu;
    }

    @Override
    public Loader<ThrowableEither<List<HomeCard>>> onCreateLoader(int id, Bundle args) {
        return new RequestAsyncTaskLoader<>(new Request(context, fragment.shouldRefresh()), context);
    }

    private static class Request extends ProcessableCacheRequest<RestoOverview, List<HomeCard>> {

        public Request(Context context, boolean shouldRefresh) {
            super(context, new RestoMenuOverviewRequest(), shouldRefresh);
        }

        @NonNull
        @Override
        protected List<HomeCard> transform(@NonNull RestoOverview data) {
            List<HomeCard> menuCardList = new ArrayList<>();

            for (RestoMenu menu: RestoOverview.filter(data, context)) {
                menuCardList.add(new RestoMenuCard(menu));
            }

            return menuCardList;
        }
    }
}