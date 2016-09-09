package be.ugent.zeus.hydra.fragments.home;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.RestoMenuCard;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.requests.resto.RestoMenuOverviewRequest;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Callback for the cards containing resto menus.
 *
 * @author Niko Strijbol
 */
class RestoMenuCallback extends CacheHomeLoaderCallback<RestoOverview> {

    public RestoMenuCallback(Context context, HomeCardAdapter adapter, FragmentCallback callback) {
        super(context, adapter, callback);
    }

    @Override
    protected List<HomeCard> convertData(@NonNull RestoOverview data) {
        List<HomeCard> menuCardList = new ArrayList<>();
        for (RestoMenu menu : data) {
            if (new DateTime(menu.getDate()).withTimeAtStartOfDay().isAfterNow()) {
                menuCardList.add(new RestoMenuCard(menu));
            }
        }
        return menuCardList;
    }

    @Override
    protected int getCardType() {
        return HomeCard.CardType.RESTO;
    }

    @Override
    protected RestoMenuOverviewRequest getCacheRequest() {
        return new RestoMenuOverviewRequest();
    }

    @Override
    protected int getErrorName() {
        return R.string.fragment_home_error_menu;
    }
}