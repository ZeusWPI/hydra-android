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

package be.ugent.zeus.hydra.schamper;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.ugent.zeus.hydra.common.ArticleViewer;
import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import com.squareup.moshi.Json;

/**
 * A Schamper article.
 *
 * @author Niko Strijbol
 * @author Feliciaan
 * @see <a href="https://schamper.ugent.be">The Schamper website</a>
 */
public record Article(
        String title,
        String link,
        @Json(name = "pub_date") OffsetDateTime pubDate,
        String author,
        String body,
        String image,
        String category,
        String intro,
        @Json(name = "category_color") String categoryColour
) implements Parcelable, ArticleViewer.Article {

    private static final Pattern IMAGE_REPLACEMENT = Pattern.compile("/regulier/", Pattern.LITERAL);

    private Article(Parcel in) {
        this(
                in.readString(),
                in.readString(),
                DateTypeConverters.toOffsetDateTime(in.readString()),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString()
        );
    }

    public LocalDateTime localPubDate() {
        return DateUtils.toLocalDateTime(pubDate());
    }

    public boolean hasCategoryColour() {
        return !TextUtils.isEmpty(categoryColour);
    }

    public String largeImage() {
        if (image != null) {
            return IMAGE_REPLACEMENT.matcher(image).replaceAll(Matcher.quoteReplacement("/preview/"));
        } else {
            return null;
        }
    }

    public String identifier() {
        return link + pubDate.toString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(DateTypeConverters.fromOffsetDateTime(pubDate));
        dest.writeString(author);
        dest.writeString(body);
        dest.writeString(image);
        dest.writeString(category);
        dest.writeString(intro);
        dest.writeString(categoryColour);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
