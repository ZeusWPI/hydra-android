package be.ugent.zeus.hydra.schamper;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.reporting.Tracker;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.preferences.ArticleFragment;
import be.ugent.zeus.hydra.utils.NetworkUtils;

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
        analytics.log(new ArticleSelectedEvent(article));

        // Open in-app or in a custom tab
        helper.openCustomTab(Uri.parse(article.getLink()));
    }

    private static class ArticleSelectedEvent implements Event {

        private final Article article;

        ArticleSelectedEvent(Article article) {
            this.article = article;
        }

        @Nullable
        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Reporting.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.contentType(), Article.class.getSimpleName());
            params.putString(names.itemId(), article.getLink());
            return params;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().selectContent();
        }
    }
}
