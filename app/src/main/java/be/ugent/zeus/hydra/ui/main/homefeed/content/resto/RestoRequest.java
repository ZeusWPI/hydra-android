package be.ugent.zeus.hydra.ui.main.homefeed.content.resto;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.ListRequest;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.data.network.requests.resto.FilteredMenuRequest;
import be.ugent.zeus.hydra.data.network.requests.resto.MenuRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class RestoRequest implements HomeFeedRequest {

    private final Request<List<RestoMenu>> request;

    public RestoRequest(Context context, boolean shouldRefresh) {
        this.request = new FilteredMenuRequest(context, new ListRequest<>(
                new CachedRequest<>(
                        context,
                        new MenuRequest(context),
                        shouldRefresh
                ))
        );
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.RESTO;
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest() throws RequestFailureException {
        return StreamSupport.stream(request.performRequest())
                .map(RestoMenuCard::new);
    }
}