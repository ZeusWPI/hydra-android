package be.ugent.zeus.hydra.fragments.home.requests;

import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.requests.common.Request;

import java.util.List;

/**
 * A request that provides {@link HomeCard}s for use in the home feed.
 *
 * @author Niko Strijbol
 */
public interface HomeFeedRequest extends Request<List<HomeCard>> {

    /**
     * @return The card type of the cards that are produced here.
     */
    @HomeCard.CardType
    int getCardType();
}