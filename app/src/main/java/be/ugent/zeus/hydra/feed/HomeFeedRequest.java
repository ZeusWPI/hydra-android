package be.ugent.zeus.hydra.feed;

import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.feed.cards.Card;

/**
 * A request that provides {@link Card}s for use in the home feed.
 *
 * @author Niko Strijbol
 */
public interface HomeFeedRequest extends Request<Stream<Card>> {

    /**
     * @return The card type of the cards that are produced here.
     */
    @Card.Type
    int getCardType();
}
