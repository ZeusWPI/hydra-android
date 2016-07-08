package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.resto.RestoMenu;
import org.joda.time.DateTime;
import org.joda.time.Duration;

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
        DateTime jodaDate = new DateTime(this.getRestoMenu().getDate());
        Duration duration = new Duration(new DateTime(), jodaDate);
        return (int) (1000 - (duration.getStandardDays()*100));
    }

    @Override
    public int getCardType() {
        return CardType.RESTO;
    }

    public RestoMenu getRestoMenu() {
        return restoMenu;
    }
}
