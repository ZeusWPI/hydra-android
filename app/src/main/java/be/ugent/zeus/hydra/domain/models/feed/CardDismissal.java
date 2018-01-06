package be.ugent.zeus.hydra.domain.models.feed;

import java8.util.Objects;
import org.threeten.bp.Instant;

/**
 * Records a dismissal of a card from the user.
 *
 * @author Niko Strijbol
 */
public class CardDismissal {

    private final CardIdentifier identifier;
    private final Instant dismissalDate;

    public CardDismissal(CardIdentifier identifier, Instant dismissalDate) {
        this.identifier = identifier;
        this.dismissalDate = dismissalDate;
    }

    public static CardDismissal dismiss(Card card) {
        return new CardDismissal(new CardIdentifier(card.getCardType(), card.getIdentifier()), Instant.now());
    }

    public CardIdentifier getIdentifier() {
        return identifier;
    }

    public Instant getDismissalDate() {
        return dismissalDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardDismissal that = (CardDismissal) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(dismissalDate, that.dismissalDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, dismissalDate);
    }

    @Override
    public String toString() {
        return "Dismissal <identifier=" + identifier + ", date=" + dismissalDate + ">";
    }
}
