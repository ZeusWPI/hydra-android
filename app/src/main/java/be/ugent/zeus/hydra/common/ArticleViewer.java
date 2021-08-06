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

package be.ugent.zeus.hydra.common;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.common.reporting.*;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;

/**
 * Provides helper methods to view an article.
 *
 * @author Niko Strijbol
 */
public class ArticleViewer {

    /**
     * Select an article for viewing.
     */
    public static void viewArticle(Context context, Article article, ActivityHelper helper) {

        // Log selection of the article.
        Tracker analytics = Reporting.getTracker(context);
        analytics.log(new ArticleViewedEvent(article));

        // Open in-app or in a custom tab
        helper.openCustomTab(Uri.parse(article.getLink()));
    }

    /**
     * Interface for basic information that article classes should implement.
     */
    public interface Article {
        /**
         * A link to the article. Must be unique.
         */
        String getLink();

        /**
         * Title of the article.
         */
        String getTitle();
    }

    private static class ArticleViewedEvent implements Event {

        private final Article article;

        ArticleViewedEvent(Article article) {
            this.article = article;
        }

        @Nullable
        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Reporting.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.contentType(), article.getClass().getSimpleName());
            params.putString(names.itemId(), article.getLink());
            params.putString(names.itemName(), article.getTitle());
            return params;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().selectContent();
        }
    }
}
