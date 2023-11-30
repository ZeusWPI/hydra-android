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

import android.os.Parcel;
import android.os.Parcelable;

import java.time.OffsetDateTime;

import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * @author Niko Strijbol
 */
@RecordBuilder
public record NewsArticle(
        String content,
        String id,
        String link,
        OffsetDateTime published,
        String summary,
        String title,
        OffsetDateTime updated
) implements Parcelable, ArticleViewer.Article, NewsArticleBuilder.With {

    private NewsArticle(Parcel in) {
        this(
                in.readString(),
                in.readString(),
                in.readString(),
                DateTypeConverters.toOffsetDateTime(in.readString()),
                in.readString(),
                in.readString(),
                DateTypeConverters.toOffsetDateTime(in.readString())
        );
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
        dest.writeString(this.id);
        dest.writeString(this.link);
        dest.writeString(DateTypeConverters.fromOffsetDateTime(this.published));
        dest.writeString(this.summary);
        dest.writeString(this.title);
        dest.writeString(DateTypeConverters.fromOffsetDateTime(this.updated));
    }


    public static final Parcelable.Creator<NewsArticle> CREATOR = new Parcelable.Creator<>() {
        @Override
        public NewsArticle createFromParcel(Parcel source) {
            return new NewsArticle(source);
        }

        @Override
        public NewsArticle[] newArray(int size) {
            return new NewsArticle[size];
        }
    };
}
