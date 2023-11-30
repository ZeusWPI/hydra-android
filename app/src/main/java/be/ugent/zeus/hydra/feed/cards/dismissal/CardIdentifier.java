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
import androidx.room.ColumnInfo;

import be.ugent.zeus.hydra.feed.cards.Card;

import java.util.Objects;

/**
 * Identifies a single card in the home feed.
 * <p>
 * A card is identified by a combination of the type and an identifier. The identifier is scoped to the type, meaning
 * an identifier can be used with multiple card types. It is not unique.
 *
 * @author Niko Strijbol
 * @noinspection ClassCanBeRecord
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
