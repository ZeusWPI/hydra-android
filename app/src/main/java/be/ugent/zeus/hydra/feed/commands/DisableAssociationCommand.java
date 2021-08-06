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

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.AssociationStore;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.feed.cards.Card;

/**
 * @author Niko Strijbol
 */
public class DisableAssociationCommand implements FeedCommand {

    private final String association;

    public DisableAssociationCommand(String association) {
        this.association = association;
    }

    @Override
    public int execute(Context context) {
        Reporting.getTracker(context).log(new DismissalEvent(association));
        AssociationStore.blacklist(context, association);
        return Card.Type.ACTIVITY;
    }

    @Override
    public int undo(Context context) {
        AssociationStore.whitelist(context, association);
        return Card.Type.ACTIVITY;
    }

    @Override
    public int getCompleteMessage() {
        return R.string.feed_card_hidden_association;
    }
}
