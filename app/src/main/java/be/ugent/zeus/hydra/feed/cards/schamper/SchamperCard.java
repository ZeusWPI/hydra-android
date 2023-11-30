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

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.schamper.Article;

import static be.ugent.zeus.hydra.feed.cards.Card.Type.SCHAMPER;

/**
 * Home card for {@link Article}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class SchamperCard extends Card {

    final Article article;

    SchamperCard(Article article) {
        this.article = article;
    }

    @Override
    public int priority() {
        OffsetDateTime date = article.pubDate();
        Duration duration = Duration.between(date, OffsetDateTime.now());
        // We only show the last month of schamper articles.
        return PriorityUtils.lerp((int) duration.toDays(), 0, 30);
    }

    @Override
    public String identifier() {
        return article.identifier();
    }

    @Override
    public int cardType() {
        return SCHAMPER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchamperCard that = (SchamperCard) o;
        return Objects.equals(article, that.article);
    }

    @Override
    public int hashCode() {
        return Objects.hash(article);
    }
}