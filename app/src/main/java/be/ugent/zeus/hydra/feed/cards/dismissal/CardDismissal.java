/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.feed.cards.dismissal;

import androidx.annotation.NonNull;
import androidx.room.*;

import java.time.Instant;
import java.util.Objects;

import be.ugent.zeus.hydra.feed.cards.Card;

/**
 * Records a dismissal of a card from the user.
 *
 * @author Niko Strijbol
 */
@Entity(tableName = DismissalTable.TABLE_NAME, indices = @Index(value = DismissalTable.Columns.CARD_TYPE))
public final class CardDismissal {

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
        return new CardDismissal(new CardIdentifier(card.cardType(), card.identifier()), Instant.now());
    }

    @NonNull
    public CardIdentifier identifier() {
        return identifier;
    }

    @NonNull
    public Instant dismissalDate() {
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
}
