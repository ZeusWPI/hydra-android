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

package be.ugent.zeus.hydra.feed.cards.news;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Objects;

import be.ugent.zeus.hydra.feed.cards.Card;
import be.ugent.zeus.hydra.feed.cards.PriorityUtils;
import be.ugent.zeus.hydra.news.NewsArticle;

/**
 * Home card for {@link be.ugent.zeus.hydra.news.NewsArticle}.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class NewsItemCard extends Card {

    private static final int TWO_WEEKS_HOURS = 14 * 24;

    private final NewsArticle newsItem;

    NewsItemCard(NewsArticle newsItem) {
        this.newsItem = newsItem;
    }

    @Override
    public int priority() {
        OffsetDateTime date = newsItem().updated();
        Duration duration = Duration.between(date, OffsetDateTime.now());
        return PriorityUtils.lerp((int) duration.toHours(), 0, TWO_WEEKS_HOURS);
    }

    @Override
    public String identifier() {
        return newsItem.id();
    }

    @Override
    public int cardType() {
        return Card.Type.NEWS_ITEM;
    }

    public NewsArticle newsItem() {
        return newsItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsItemCard that = (NewsItemCard) o;
        return Objects.equals(newsItem, that.newsItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newsItem);
    }
}
