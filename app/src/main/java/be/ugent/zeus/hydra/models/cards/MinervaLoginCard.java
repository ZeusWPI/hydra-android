package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.adapters.HomeCardAdapter;

/**
 * Created by feliciaan on 29/06/16.
 */
public class MinervaLoginCard extends HomeCard {
    @Override
    public int getPriority() {
        return 850; //TODO: add setting to hide!
    }

    @Override
    public HomeCardAdapter.HomeType getCardType() {
        return HomeCardAdapter.HomeType.MINERVALOGIN;
    }
}
