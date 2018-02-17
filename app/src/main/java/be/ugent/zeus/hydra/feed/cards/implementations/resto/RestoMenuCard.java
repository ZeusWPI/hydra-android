package be.ugent.zeus.hydra.feed.cards.implementations.resto;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.menu.SelectableMetaRequest;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import java8.util.Objects;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * Home card for {@link RestoMenu}.
 *
 * @author Niko Strijbol
 * @author silox
 */
class RestoMenuCard extends Card {

    // From 11 h to 15 h we are more interested in the menu.
    private static final LocalDateTime interestStart = LocalDateTime.now().withHour(11).withMinute(0);
    private static final LocalDateTime interestEnd = LocalDateTime.now().withHour(15).withMinute(0);

    private final RestoMenu restoMenu;
    private final SelectableMetaRequest.RestoChoice restoChoice;

    RestoMenuCard(RestoMenu restoMenu, SelectableMetaRequest.RestoChoice choice) {
        this.restoMenu = restoMenu;
        this.restoChoice = choice;
    }

    RestoMenu getRestoMenu() {
        return restoMenu;
    }

    SelectableMetaRequest.RestoChoice getRestoChoice() {
        return restoChoice;
    }

    @Override
    public int getPriority() {
        LocalDateTime now = LocalDateTime.now();
        int duration = (int) ChronoUnit.DAYS.between(now.toLocalDate(), restoMenu.getDate());
        if (now.isAfter(interestStart) && now.isBefore(interestEnd)) {
            return Math.max(PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.lerp((int) ((duration - 0.5) * 24), 0, 504)) - 5;
        } else {
            return Math.max(PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.lerp((int) ((duration - 0.5) * 24), 0, 504));
        }

    }

    @Override
    public String getIdentifier() {
        // Two resto's for the same day are equal, regardless of the resto.
        return restoMenu.getDate().toString();
    }

    @Override
    public int getCardType() {
        return Card.Type.RESTO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoMenuCard that = (RestoMenuCard) o;
        return Objects.equals(restoMenu, that.restoMenu) &&
                Objects.equals(restoChoice, that.restoChoice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restoMenu, restoChoice);
    }
}