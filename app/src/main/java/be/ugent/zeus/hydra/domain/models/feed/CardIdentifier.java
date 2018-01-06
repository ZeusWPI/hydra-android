package be.ugent.zeus.hydra.domain.models.feed;

import java8.util.Objects;

/**
 * Identifies a single card in the home feed.
 *
 * TODO: is it necessary to introduce a DTO object here?
 *
 * A card is identified by a combination of the type and an identifier. The identifier is scoped to the type, meaning
 * an identifier can be used with multiple card types. It is not unique.
 *
 * @author Niko Strijbol
 */
public class CardIdentifier {

    @Card.Type
    private final int cardType;
    private final String identifier;

    public CardIdentifier(int cardType, String identifier) {
        this.cardType = cardType;
        this.identifier = identifier;
    }

    /**
     * @return Get the type of this card identifier.
     */
    @Card.Type
    public int getCardType() {
        return cardType;
    }

    /**
     * @return Get the identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardIdentifier that = (CardIdentifier) o;
        return cardType == that.cardType && Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardType, identifier);
    }

    @Override
    public String toString() {
        return "Identifier <type=" + getCardType() + ", identifier=" + getIdentifier() + ">";
    }
}