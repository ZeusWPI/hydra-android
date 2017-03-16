package be.ugent.zeus.hydra.homefeed.content.resto;

import android.content.Context;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.data.models.resto.RestoOverview;
import be.ugent.zeus.hydra.data.network.ProcessableCacheRequest;
import be.ugent.zeus.hydra.data.network.requests.resto.RestoMenuRequest;
import java8.util.stream.Stream;
import java8.util.stream.StreamSupport;

/**
 * @author Niko Strijbol
 */
public class RestoRequest extends ProcessableCacheRequest<RestoOverview, Stream<HomeCard>> implements HomeFeedRequest {

    public RestoRequest(Context context, boolean shouldRefresh) {
        super(context, new RestoMenuRequest(context), shouldRefresh);
    }

    @NonNull
    @Override
    protected Stream<HomeCard> transform(@NonNull RestoOverview data) {
        return RestoOverview.filter(StreamSupport.stream(data), context)
                .map(RestoMenuCard::new);
    }

    @Override
    public int getCardType() {
        return HomeCard.CardType.RESTO;
    }
}