package be.ugent.zeus.hydra.ui.main.homefeed.content.resto;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.requests.Requests;
import be.ugent.zeus.hydra.data.network.requests.resto.MenuFilter;
import be.ugent.zeus.hydra.data.network.requests.resto.MenuRequest;
import be.ugent.zeus.hydra.data.network.requests.Result;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class RestoRequest implements HomeFeedRequest {

    private final Request<List<RestoMenu>> request;

    public RestoRequest(Context context) {
        this.request = Requests.map(
                Requests.map(Requests.cache(context, new MenuRequest(context)), Arrays::asList),
                new MenuFilter(context)
        );
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.RESTO;
    }

    @NonNull
    @Override
    public Result<Stream<HomeCard>> performRequest(Bundle args) {
        return request.performRequest(args).map(restoMenus -> StreamSupport.stream(restoMenus)
                .map(RestoMenuCard::new));
    }
}