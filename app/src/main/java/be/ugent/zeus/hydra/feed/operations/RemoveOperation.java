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

package be.ugent.zeus.hydra.feed.operations;

import android.os.Bundle;
import androidx.annotation.NonNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.cards.Card;

/**
 * Operation that removes all cards of a type from the home feed.
 *
 * @author Niko Strijbol
 */
class RemoveOperation implements FeedOperation {

    @Card.Type
    private final int cardType;
    private final Predicate<Card> predicate;

    RemoveOperation(@Card.Type int cardType) {
        this(cardType, c -> c.cardType() == cardType);
    }

    private RemoveOperation(@Card.Type int cardType, Predicate<Card> predicate) {
        this.cardType = cardType;
        this.predicate = predicate;
    }

    @NonNull
    @Override
    public Result<List<Card>> transform(Bundle args, List<Card> current) {
        return Result.Builder.fromData(current.stream().filter(predicate.negate()).collect(Collectors.toList()));
    }

    @Override
    public int cardType() {
        return cardType;
    }

    @NonNull
    @Override
    public String toString() {
        return "REMOVE -> Card Type " + cardType;
    }
}
