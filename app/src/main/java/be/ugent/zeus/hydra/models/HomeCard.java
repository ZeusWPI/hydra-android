package be.ugent.zeus.hydra.models;

import be.ugent.zeus.hydra.adapters.HomeCardAdapter;

/**
 * Add models to the interface
 * Created by feliciaan on 06/04/16.
 */
public interface HomeCard {

    /**
     *
     * @return Priority should be a number between min inf and 1000.
     */
    public int getPriority();

    /**
     * Return respective cardType for the Card
     */
    public HomeCardAdapter.HomeType getCardType();
}
