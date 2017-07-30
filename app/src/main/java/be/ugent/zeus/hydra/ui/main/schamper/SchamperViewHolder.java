package be.ugent.zeus.hydra.ui.main.schamper;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.schamper.Article;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.ui.preferences.ArticlePreferenceFragment;
import be.ugent.zeus.hydra.ui.SchamperArticleActivity;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * View holder for the schamper fragment.
 *
 * @author Niko Strijbol
 */
class SchamperViewHolder extends DataViewHolder<Article> {

    private final TextView title;
    private final TextView date;
    private final TextView author;
    private final TextView category;
    private final ImageView image;

    private final ActivityHelper helper;
    private final SharedPreferences preferences;

    SchamperViewHolder(View itemView, ActivityHelper helper) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        date = itemView.findViewById(R.id.date);
        author = itemView.findViewById(R.id.author);
        image = itemView.findViewById(R.id.card_image);
        category = itemView.findViewById(R.id.schamper_category);
        this.helper = helper;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
    }

    public void populate(final Article article) {
        title.setText(article.getTitle());
        date.setText(DateUtils.relativeDateTimeString(article.getPubDate(), itemView.getContext()));
        author.setText(article.getAuthor());
        category.setText(article.getCategory());

        if (NetworkUtils.isMeteredConnection(itemView.getContext())) {
            Picasso.with(this.itemView.getContext()).load(article.getImage()).into(image);
        } else {
            Picasso.with(this.itemView.getContext()).load(article.getLargeImage()).into(image);
        }

        this.itemView.setOnClickListener(v -> {
            // The user has the choice to open the article in app or not. If offline, always open in app.
            boolean useCustomTabs = preferences.getBoolean(ArticlePreferenceFragment.PREF_USE_CUSTOM_TABS, ArticlePreferenceFragment.PREF_USE_CUSTOM_TABS_DEFAULT);
            boolean isOnline = NetworkUtils.isConnected(v.getContext());

            if (useCustomTabs && isOnline) {
                // Open in Custom tabs.
                helper.openCustomTab(Uri.parse(article.getLink()));
            } else {
                SchamperArticleActivity.launchWithAnimation(helper.getActivity(), image, "hero", article);
            }
        });
    }
}