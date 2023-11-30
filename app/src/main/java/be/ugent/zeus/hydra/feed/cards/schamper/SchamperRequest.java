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

package be.ugent.zeus.hydra.feed.cards.schamper;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.feed.HideableHomeFeedRequest;
import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;
import be.ugent.zeus.hydra.schamper.Article;
import be.ugent.zeus.hydra.schamper.SchamperArticlesRequest;

/**
 * @author Niko Strijbol
 */
public class SchamperRequest extends HideableHomeFeedRequest {

    private final Request<List<Article>> request;

    public SchamperRequest(Context context, DismissalDao dismissalDao) {
        super(dismissalDao);
        this.request = new SchamperArticlesRequest(context);
    }

    @Override
    public int cardType() {
        return Card.Type.SCHAMPER;
    }

    @NonNull
    @Override
    protected Result<Stream<Card>> performRequestCards(@NonNull Bundle args) {
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minusMonths(1);

        return request.execute(args).map(articles -> articles.stream()
                .filter(a -> a.localPubDate().isAfter(twoMonthsAgo))
                .map(SchamperCard::new));
    }
}
