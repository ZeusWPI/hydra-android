package be.ugent.zeus.hydra.feed.cards.resto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;

/**
 * Home card for {@link RestoMenu}.
 *
 * @author Niko Strijbol
 * @author silox
 */
class RestoMenuCard extends Card {

    // From 10:30 h to 14:30 h we are more interested in the menu.
    private static final LocalDateTime interestStart = LocalDateTime.now().withHour(10).withMinute(30);
    private static final LocalDateTime interestEnd = LocalDateTime.now().withHour(14).withMinute(30);

    private final RestoMenu restoMenu;
    private final RestoChoice restoChoice;
    private final String feedRestoKind;

    RestoMenuCard(RestoMenu restoMenu, RestoChoice choice, String feedRestoKind) {
        this.restoMenu = restoMenu;
        this.restoChoice = choice;
        this.feedRestoKind = feedRestoKind;
    }

    RestoMenu getRestoMenu() {
        return restoMenu;
    }

    RestoChoice getRestoChoice() {
        return restoChoice;
    }

    @Override
    public int getPriority() {
        LocalDateTime now = LocalDateTime.now();
        int duration = (int) ChronoUnit.DAYS.between(now.toLocalDate(), restoMenu.getDate());
        if (now.isAfter(interestStart) && now.isBefore(interestEnd)) {
            return Math.max(PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.lerp((int) ((duration - 0.5) * 24), 0, 504)) - 5;
        } else {
            return Math.max(PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.lerp((int) ((duration - 0.5) * 24), 0, 504)) + 3;
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
                Objects.equals(restoChoice, that.restoChoice) &&
                Objects.equals(feedRestoKind, that.feedRestoKind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restoMenu, restoChoice, feedRestoKind);
    }
}