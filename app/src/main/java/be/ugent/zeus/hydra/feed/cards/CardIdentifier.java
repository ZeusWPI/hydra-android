package be.ugent.zeus.hydra.feed.cards;

import androidx.room.ColumnInfo;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.feed.cards.database.DismissalTable;
import java9.util.Objects;

/**
 * Identifies a single card in the home feed.
 *
 * A card is identified by a combination of the type and an identifier. The identifier is scoped to the type, meaning
 * an identifier can be used with multiple card types. It is not unique.
 *
 * @author Niko Strijbol
 */
public final class CardIdentifier {

    @Card.Type
    @ColumnInfo(name = DismissalTable.Columns.CARD_TYPE)
    private final int cardType;
    @NonNull
    @ColumnInfo(name = DismissalTable.Columns.IDENTIFIER)
    private final String identifier;

    public CardIdentifier(@Card.Type int cardType, @NonNull String identifier) {
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
    @NonNull
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
}
