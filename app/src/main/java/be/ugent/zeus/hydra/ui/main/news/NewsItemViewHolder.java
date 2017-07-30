package be.ugent.zeus.hydra.ui.main.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.ui.NewsArticleActivity;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.html.Utils;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.preferences.NewsPreferenceFragment;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;

import static be.ugent.zeus.hydra.ui.NewsArticleActivity.PARCEL_NAME;

/**
 * View holder for the news items in the news tab or section.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
class NewsItemViewHolder extends DataViewHolder<UgentNewsItem> {

    private final TextView info;
    private final TextView title;
    private final TextView excerpt;
    private final SharedPreferences preferences;
    private final ActivityHelper helper;

    NewsItemViewHolder(View v, ActivityHelper activityHelper) {
        super(v);
        title = v.findViewById(R.id.name);
        info = v.findViewById(R.id.info);
        excerpt = v.findViewById(R.id.article_excerpt);
        preferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
        this.helper = activityHelper;
    }

    @Override
    public void populate(final UgentNewsItem newsItem) {

        title.setText(newsItem.getTitle());

        String author = newsItem.getCreators().isEmpty() ? "" : newsItem.getCreators().iterator().next();

        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                DateUtils.relativeDateTimeString(newsItem.getCreated(), itemView.getContext()),
                author);
        info.setText(infoText);

        if (!TextUtils.isEmpty(newsItem.getDescription())) {
            excerpt.setText(Utils.fromHtml(newsItem.getDescription()).toString().trim());
        } else {
            excerpt.setText(Utils.fromHtml(newsItem.getText()).toString().trim());
        }

        itemView.setOnClickListener(v -> {

            // The user has the choice to open the article in app or not. If offline, always open in app.
            boolean useCustomTabs = preferences.getBoolean(NewsPreferenceFragment.PREF_USE_CUSTOM_TABS, NewsPreferenceFragment.PREF_USE_CUSTOM_TABS_DEFAULT);
            boolean isOnline = NetworkUtils.isConnected(v.getContext());
            if (useCustomTabs && isOnline) {
                // Open in Custom tabs.
                helper.openCustomTab(Uri.parse(newsItem.getIdentifier()));
            } else {
                Intent intent = new Intent(v.getContext(), NewsArticleActivity.class);
                intent.putExtra(PARCEL_NAME, (Parcelable) newsItem);
                v.getContext().startActivity(intent);
            }
        });
    }
}