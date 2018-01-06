package be.ugent.zeus.hydra.domain.models.feed;

import android.arch.persistence.room.*;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.database.feed.DismissalTable;
import java8.util.Objects;
import org.threeten.bp.Instant;

/**
 * Records a dismissal of a card from the user.
 *
 * @author Niko Strijbol
 */
@Entity(tableName = DismissalTable.TABLE_NAME, indices = @Index(value = DismissalTable.Columns.CARD_TYPE))
public class CardDismissal {

    @NonNull
    @Embedded
    @PrimaryKey
    private final CardIdentifier identifier;
    @NonNull
    @ColumnInfo(name = DismissalTable.Columns.DISMISSAL_DATE)
    private final Instant dismissalDate;

    public CardDismissal(@NonNull CardIdentifier identifier, @NonNull Instant dismissalDate) {
        this.identifier = identifier;
        this.dismissalDate = dismissalDate;
    }

    public static CardDismissal dismiss(Card card) {
        return new CardDismissal(new CardIdentifier(card.getCardType(), card.getIdentifier()), Instant.now());
    }

    @NonNull
    public CardIdentifier getIdentifier() {
        return identifier;
    }

    @NonNull
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