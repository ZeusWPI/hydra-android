package be.ugent.zeus.hydra.ui.main.homefeed.content.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.ui.NewsArticleActivity;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.preferences.ArticlePreferenceFragment;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;

import static be.ugent.zeus.hydra.ui.NewsArticleActivity.PARCEL_NAME;

/**
 * View holder for the news card in the home feed.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class NewsItemViewHolder extends FeedViewHolder {

    private final TextView info;
    private final TextView title;
    private final SharedPreferences preferences;

    public NewsItemViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = v.findViewById(R.id.name);
        info = v.findViewById(R.id.info);
        preferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
    }

    @Override
    public void populate(final HomeCard card) {
        super.populate(card);

        UgentNewsItem newsItem = card.<NewsItemCard>checkCard(HomeCard.CardType.NEWS_ITEM).getNewsItem();

        title.setText(newsItem.getTitle());

        String author = newsItem.getCreators().isEmpty() ? "" : newsItem.getCreators().iterator().next();

        String infoText = itemView.getContext().getString(R.string.agenda_subtitle,
                DateUtils.relativeDateTimeString(newsItem.getCreated(), itemView.getContext()),
                author);
        info.setText(infoText);
        title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        itemView.setOnClickListener(v -> {

            // The user has the choice to open the article in app or not. If offline, always open in app.
            boolean useCustomTabs = preferences.getBoolean(ArticlePreferenceFragment.PREF_USE_CUSTOM_TABS, ArticlePreferenceFragment.PREF_USE_CUSTOM_TABS_DEFAULT);
            boolean isOnline = NetworkUtils.isConnected(v.getContext());
            if (useCustomTabs && isOnline) {
                // Open in Custom tabs.
                adapter.getCompanion().getHelper().openCustomTab(Uri.parse(newsItem.getIdentifier()));
            } else {
                Intent intent = new Intent(v.getContext(), NewsArticleActivity.class);
                intent.putExtra(PARCEL_NAME, (Parcelable) newsItem);
                v.getContext().startActivity(intent);
            }
        });
    }
}