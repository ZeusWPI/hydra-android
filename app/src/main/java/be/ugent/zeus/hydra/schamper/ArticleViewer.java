package be.ugent.zeus.hydra.schamper;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;

import be.ugent.zeus.hydra.common.analytics.Analytics;
import be.ugent.zeus.hydra.common.analytics.BaseEvents;
import be.ugent.zeus.hydra.common.analytics.Event;
import be.ugent.zeus.hydra.common.analytics.Tracker;
import be.ugent.zeus.hydra.common.article.CustomTabPreferenceFragment;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.utils.NetworkUtils;

/**
 * @author Niko Strijbol
 */
public class ArticleViewer {
    /**
     * Select an article for viewing.
     */
    public static void viewArticle(Context context, Article article, ActivityHelper helper) {

        // Log selection of the article.
        Tracker analytics = Analytics.getTracker(context);
        analytics.log(new ArticleSelectedEvent(article));

        // Open in-app or in a custom tab
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useCustomTabs = preferences.getBoolean(CustomTabPreferenceFragment.PREF_USE_CUSTOM_TABS, CustomTabPreferenceFragment.PREF_USE_CUSTOM_TABS_DEFAULT);
        boolean isOnline = NetworkUtils.isConnected(context);

        if (useCustomTabs && isOnline) {
            helper.openCustomTab(Uri.parse(article.getLink()));
        } else {
            FullArticleActivity.start(context, article);
        }
    }

    private static class ArticleSelectedEvent implements Event {

        private final Article article;

        ArticleSelectedEvent(Article article) {
            this.article = article;
        }

        @Nullable
        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Analytics.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.contentType(), Article.class.getSimpleName());
            params.putString(names.itemId(), article.getLink());
            return params;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Analytics.getEvents().selectContent();
        }
    }
}
