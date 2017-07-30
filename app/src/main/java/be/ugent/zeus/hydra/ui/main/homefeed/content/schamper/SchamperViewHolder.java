package be.ugent.zeus.hydra.ui.main.homefeed.content.schamper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.schamper.Article;
import be.ugent.zeus.hydra.ui.main.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedUtils;
import be.ugent.zeus.hydra.ui.main.homefeed.content.FeedViewHolder;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.ui.preferences.ArticlePreferenceFragment;
import be.ugent.zeus.hydra.ui.schamper.SchamperArticleActivity;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;

/**
 * Home feed view holder for Schamper articles.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class SchamperViewHolder extends FeedViewHolder {

    private static final String TAG = "SchamperViewHolder";

    private final TextView title;
    private final TextView date;
    private final TextView author;
    private final ImageView image;
    private final SharedPreferences preferences;

    public SchamperViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        title = v.findViewById(R.id.title);
        date = v.findViewById(R.id.date);
        author = v.findViewById(R.id.author);
        image = v.findViewById(R.id.image);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(v.getContext());
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        Article article = card.<SchamperCard>checkCard(HomeCard.CardType.SCHAMPER).getArticle();

        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateTimeString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());

        FeedUtils.loadThumbnail(itemView.getContext(), article.getImage(), image);

        this.itemView.setOnClickListener(v -> {
            // The user has the choice to open the article in app or not. If offline, always open in app.
            boolean useCustomTabs = preferences.getBoolean(ArticlePreferenceFragment.PREF_USE_CUSTOM_TABS, ArticlePreferenceFragment.PREF_USE_CUSTOM_TABS_DEFAULT);
            boolean isOnline = NetworkUtils.isConnected(v.getContext());

            if (useCustomTabs && isOnline) {
                // Open in Custom tabs.
                adapter.getCompanion().getHelper().openCustomTab(Uri.parse(article.getLink()));
            } else {
                Intent intent = new Intent(v.getContext(), SchamperArticleActivity.class);
                intent.putExtra(SchamperArticleActivity.PARCEL_ARTICLE, (Parcelable) article);
                v.getContext().startActivity(intent);
            }
        });
    }
}