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

package be.ugent.zeus.hydra.news;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author Niko Strijbol
 * @noinspection unused
 */
public final class NewsStream {

    private String generator;
    private String id;
    private String language;
    private String link;
    private String logo;
    private String title;
    private OffsetDateTime updated;

    private List<NewsArticle> entries;

    @SuppressWarnings("unused")
    public NewsStream() {
        // Moshi constructor
    }

    public List<NewsArticle> getEntries() {
        return entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsStream that = (NewsStream) o;
        return Objects.equals(generator, that.generator)
                && id.equals(that.id)
                && Objects.equals(language, that.language)
                && Objects.equals(link, that.link)
                && Objects.equals(logo, that.logo)
                && Objects.equals(title, that.title)
                && updated.equals(that.updated)
                && entries.equals(that.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(generator, id, language, link, logo, title, updated, entries);
    }
}
