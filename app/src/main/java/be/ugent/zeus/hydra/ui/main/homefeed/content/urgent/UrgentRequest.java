package be.ugent.zeus.hydra.ui.main.homefeed.content.urgent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedRequest;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import java8.util.stream.RefStreams;
import java8.util.stream.Stream;

/**
 * @author Niko Strijbol
 */
public class UrgentRequest implements HomeFeedRequest {

    @Override
    public int getCardType() {
        return HomeCard.CardType.URGENT_FM;
    }

    @NonNull
    @Override
    public Stream<HomeCard> performRequest(Bundle args) throws RequestFailureException {
        return RefStreams.of(new UrgentCard());
    }
}