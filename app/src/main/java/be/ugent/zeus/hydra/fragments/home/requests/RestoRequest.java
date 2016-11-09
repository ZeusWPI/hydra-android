package be.ugent.zeus.hydra.fragments.home.requests;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.RestoMenuCard;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.models.resto.RestoOverview;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import be.ugent.zeus.hydra.requests.resto.RestoMenuRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class RestoRequest extends ProcessableCacheRequest<RestoOverview, List<HomeCard>> implements HomeFeedRequest {

    public RestoRequest(Context context, boolean shouldRefresh) {
        super(context, new RestoMenuRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected List<HomeCard> transform(@NonNull RestoOverview data) {
        List<HomeCard> menuCardList = new ArrayList<>();
        RestoOverview.filter(data, context);

        for (RestoMenu menu: data) {
            menuCardList.add(new RestoMenuCard(menu));
        }

        return menuCardList;
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.RESTO;
    }
}
