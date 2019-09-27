package be.ugent.zeus.hydra.association.news;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.common.ui.html.PicassoImageGetter;
import be.ugent.zeus.hydra.common.ui.html.Utils;
import be.ugent.zeus.hydra.preferences.ArticleFragment;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;

/**
 * Display a news article from DSA.
 *
 * @author Niko Strijbol
 */
public class NewsArticleActivity extends BaseActivity {

    @VisibleForTesting
    public static final String PARCEL_NAME = "newsItem";

    private ActivityHelper helper;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_article);

        Intent intent = getIntent();
        UgentNewsArticle article = intent.getParcelableExtra(PARCEL_NAME);
        assert article != null;

        this.url = article.getIdentifier();

        TextView title = findViewById(R.id.title);
        TextView date = findViewById(R.id.date);
        TextView text = findViewById(R.id.text);
        TextView author = findViewById(R.id.author);
        TextView lead = findViewById(R.id.article_lead);

        author.setText(TextUtils.join(", ", article.getCreators()));

        CharSequence dateString;
        if (article.getCreated().toLocalDate().isEqual(article.getModified().toLocalDate())) {
            dateString = DateUtils.relativeDateTimeString(article.getCreated(), this);
        } else {
            dateString = getString(R.string.article_date_changed,
                    DateUtils.relativeDateTimeString(article.getCreated(), this),
                    DateUtils.relativeDateTimeString(article.getModified(), this)
            );
        }
        date.setText(dateString);

        if (!TextUtils.isEmpty(article.getDescription())) {
            lead.setText(Utils.fromHtml(article.getDescription(), new PicassoImageGetter(lead, getResources())));
        } else {
            lead.setVisibility(View.GONE);
        }

        if (article.getText() != null) {
            text.setText(Utils.fromHtml(article.getText(), new PicassoImageGetter(text, getResources())));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (article.getTitle() != null) {
            title.setText(article.getTitle());
        }

        helper = CustomTabsHelper.initHelper(this, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_news, menu);
        tintToolbarIcons(menu, R.id.article_link);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.article_link) {
            helper.openCustomTab(Uri.parse(url));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        helper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        helper.unbindCustomTabsService(this);
    }

    /**
     * Open the article for viewing, depending on the network status and the user's preference.
     *
     * @param context A context.
     * @param article The article to open.
     * @param helper  Helper for opening custom tabs.
     */
    public static void viewArticle(Context context, UgentNewsArticle article, ActivityHelper helper) {

        // Log viewing the article
        Reporting.getTracker(context).log(new ArticleViewedEvent(article));

        // Open in-app or in a custom tab
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useCustomTabs = preferences.getBoolean(ArticleFragment.PREF_USE_CUSTOM_TABS, ArticleFragment.PREF_USE_CUSTOM_TABS_DEFAULT);
        boolean isOnline = NetworkUtils.isConnected(context);
        if (useCustomTabs && isOnline) {
            // Open in Custom tabs.
            helper.openCustomTab(Uri.parse(article.getIdentifier()));
        } else {
            Intent intent = new Intent(context, NewsArticleActivity.class);
            intent.putExtra(PARCEL_NAME, article);
            context.startActivity(intent);
        }
    }

    private static final class ArticleViewedEvent implements Event {

        private final UgentNewsArticle article;

        private ArticleViewedEvent(UgentNewsArticle article) {
            this.article = article;
        }

        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Reporting.getEvents().params();
            Bundle bundle = new Bundle();
            bundle.putString(names.itemCategory(), UgentNewsArticle.class.getSimpleName());
            bundle.putString(names.itemId(), article.getIdentifier());
            bundle.putString(names.itemName(), article.getTitle());
            return bundle;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().viewItem();
        }
    }
}
