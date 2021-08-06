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

package be.ugent.zeus.hydra.feed.commands;

import android.content.Context;

import java.util.function.Function;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;

/**
 * @author Niko Strijbol
 */
public class DisableIndividualCard implements FeedCommand {

    private final CardDismissal cardDismissal;
    private final Function<Context, DismissalDao> daoSupplier;

    public DisableIndividualCard(Card card) {
        this(CardDismissal.dismiss(card), c -> Database.get(c).getCardDao());
    }

    public DisableIndividualCard(CardDismissal card, Function<Context, DismissalDao> daoSupplier) {
        this.cardDismissal = card;
        this.daoSupplier = daoSupplier;
    }

    @Override
    public int execute(Context context) {
        Reporting.getTracker(context).log(new DismissalEvent(cardDismissal.getIdentifier()));
        DismissalDao dao = daoSupplier.apply(context);
        dao.insert(cardDismissal);
        return cardDismissal.getIdentifier().getCardType();
    }

    @Override
    public int undo(Context context) {
        DismissalDao dao = daoSupplier.apply(context);
        dao.delete(cardDismissal);
        return cardDismissal.getIdentifier().getCardType();
    }

    @Override
    public int getCompleteMessage() {
        return R.string.feed_card_hidden_single;
    }
}
