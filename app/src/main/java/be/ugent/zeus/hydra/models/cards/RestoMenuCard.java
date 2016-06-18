package be.ugent.zeus.hydra.models.cards;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.resto.RestoMenu;

/**
 * Created by silox on 18/04/16.
 */
public class RestoMenuCard extends HomeCard {

    private RestoMenu restoMenu;

    public RestoMenuCard(RestoMenu restoMenu) {
        this.restoMenu = restoMenu;
    }

    @Override
    public int getPriority() {
        DateTime jodadate = new DateTime(this.getRestoMenu().getDate());
        Duration duration = new Duration(new DateTime(), jodadate);
        return (int) (1000 - (duration.getStandardDays()*100));
    }

    @Override
    public HomeCardAdapter.HomeType getCardType() {
        return HomeCardAdapter.HomeType.RESTO;
    }

    public RestoMenu getRestoMenu() {
        return restoMenu;
    }
}
