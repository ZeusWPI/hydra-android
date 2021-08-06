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

import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.common.converter.DateTypeConverters;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * @author Niko Strijbol
 */
public final class NewsArticle implements Parcelable, ArticleViewer.Article {
    
    private String content;
    private String id;
    private String link;
    private OffsetDateTime published;
    private String summary;
    private String title;
    private OffsetDateTime updated;
    
    public NewsArticle() {
        // Moshi constructor
    }

    public OffsetDateTime getPublished() {
        return published;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public OffsetDateTime getUpdated() {
        return updated;
    }

    @Override
    public String getLink() {
        return link;
    }
    
    public String getSummary() {
        return summary;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setUpdated(OffsetDateTime updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsArticle that = (NewsArticle) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    protected NewsArticle(Parcel in) {
        this.content = in.readString();
        this.id = in.readString();
        this.link = in.readString();
        this.published = DateTypeConverters.toOffsetDateTime(in.readString());
        this.summary = in.readString();
        this.title = in.readString();
        this.updated = DateTypeConverters.toOffsetDateTime(in.readString());
    }

    public static final Parcelable.Creator<NewsArticle> CREATOR = new Parcelable.Creator<NewsArticle>() {
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
