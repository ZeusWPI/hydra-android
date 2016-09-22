package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.resto.RestoMenu;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;

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
        LocalDate date = getRestoMenu().getDate();
        Period duration = Period.between(LocalDate.now(), date);
        return 1000 - (duration.getDays() * 100);
    }

    @Override
    public int getCardType() {
        return CardType.RESTO;
    }

    public RestoMenu getRestoMenu() {
        return restoMenu;
    }
}
