package be.ugent.zeus.hydra.homefeed.content.resto;

import be.ugent.zeus.hydra.homefeed.content.FeedUtils;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.models.resto.RestoMenu;
import java8.util.Objects;
import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * Home card for {@link RestoMenu}.
 *
 * @author Niko Strijbol
 * @author silox
 */
class RestoMenuCard extends HomeCard {

    private final RestoMenu restoMenu;

    RestoMenuCard(RestoMenu restoMenu) {
        this.restoMenu = restoMenu;
    }

    RestoMenu getRestoMenu() {
        return restoMenu;
    }

    @Override
    public int getPriority() {
        int duration = (int) ChronoUnit.DAYS.between(LocalDate.now(), restoMenu.getDate());
        return Math.max(0, FeedUtils.lerp((int) ((duration - 0.5) * 24), 0, 504));
    }

    @Override
    public int getCardType() {
        return CardType.RESTO;
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