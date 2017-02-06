package be.ugent.zeus.hydra.homefeed.content.urgent;

import be.ugent.zeus.hydra.homefeed.content.HomeCard;

/**
 * @author Niko Strijbol
 */
public class UrgentCard extends HomeCard {

    @Override
    public int getPriority() {
        return -5;
    }

    @Override
    public int getCardType() {
        return CardType.URGENT_FM;
    }


}