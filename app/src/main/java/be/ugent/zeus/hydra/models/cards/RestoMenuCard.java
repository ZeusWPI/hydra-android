package be.ugent.zeus.hydra.models.cards;

import be.ugent.zeus.hydra.models.resto.RestoMenu;
import be.ugent.zeus.hydra.utils.Objects;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Period;

/**
 * Home card for {@link RestoMenu}.
 *
 * @author Niko Strijbol
 * @author silox
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoMenuCard that = (RestoMenuCard) o;
        return Objects.equals(restoMenu, that.restoMenu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restoMenu);
    }
}