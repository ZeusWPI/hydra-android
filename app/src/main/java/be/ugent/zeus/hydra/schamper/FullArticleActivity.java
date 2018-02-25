package be.ugent.zeus.hydra.schamper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.common.ui.html.PicassoImageGetter;
import be.ugent.zeus.hydra.common.ui.html.Utils;
import be.ugent.zeus.hydra.association.news.ArticlePreferenceFragment;
import be.ugent.zeus.hydra.utils.Analytics;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.utils.StringUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

public class FullArticleActivity extends BaseActivity {

    private static final String PARCEL_ARTICLE = "article";

    private Article article;

    /**
     * Launch this activity with a transition.
     *
     * @param activity The activity that launches the intent.
     * @param view     The view to transition.
     * @param name     The name of the transition.
     * @param article  The article.
     */
    private static void launchWithAnimation(Activity activity, View view, String name, Parcelable article) {
        Intent intent = new Intent(activity, FullArticleActivity.class);
        intent.putExtra(PARCEL_ARTICLE, article);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, name);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schamper_article);

        customFade();

        Intent intent = getIntent();
        article = intent.getParcelableExtra(PARCEL_ARTICLE);

        TextView title = findViewById(R.id.title);
        TextView date = findViewById(R.id.date);
        TextView text = findViewById(R.id.text);
        TextView intro = findViewById(R.id.intro);
        TextView author = findViewById(R.id.author);


        ImageView headerImage = findViewById(R.id.header_image);

        if (article.getImage() != null) {
            Picasso.with(this).load(article.getImage()).into(headerImage);
        }

        if (article.getAuthor() != null) {
            author.setText(article.getAuthor());
        }

        String category = StringUtils.capitaliseFirst(article.getCategory());
        if (article.getPubDate() != null) {
            date.setText(DateUtils.relativeDateTimeString(article.getPubDate(), date.getContext()) + " - " + category);
        } else {
            date.setText(category);
        }

        if (article.getBody() != null) {

            //The intro
            if (TextUtils.isEmpty(article.getIntro())) {
                findViewById(R.id.intro_wrapper).setVisibility(View.GONE);
            } else {
                intro.setText(Utils.fromHtml(article.getIntro()));
                intro.setMovementMethod(LinkMovementMethod.getInstance());
            }

            //The body
            text.setText(Utils.fromHtml(article.getBody(), new PicassoImageGetter(text, getResources(), this)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (article.getTitle() != null) {
            title.setText(article.getTitle());
            getSupportActionBar().setTitle(article.getTitle());
        }
    }

    @Override
    protected String getScreenName() {
        return "Schamper article > " + article.getTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schamper, menu);
        tintToolbarIcons(menu, R.id.schamper_share, R.id.schamper_browser);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            //Up button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            //Share button
            case R.id.schamper_share:
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, article.getLink());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Deel het artikel met…"));
                return true;
            //Open in browser
            case R.id.schamper_browser:
                NetworkUtils.maybeLaunchBrowser(this, article.getLink());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Set a custom fade when using transition to prevent white flashing/blinking. This excludes the status bar and
     * navigation bar background from the animation.
     */
    private void customFade() {
        //Only do it on a version that is high enough.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);
        }
    }

    public static void viewArticle(Context context, Article article, ActivityHelper helper, View image) {

        // Log viewing the article
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(context);
        Bundle parameters = new Bundle();
        parameters.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, Analytics.Type.SCHAMPER_ARTICLE);
        parameters.putString(FirebaseAnalytics.Param.ITEM_NAME, article.getTitle());
        parameters.putString(FirebaseAnalytics.Param.ITEM_ID, article.getLink());
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, parameters);

        // Open in-app or in a custom tab
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean useCustomTabs = preferences.getBoolean(ArticlePreferenceFragment.PREF_USE_CUSTOM_TABS, ArticlePreferenceFragment.PREF_USE_CUSTOM_TABS_DEFAULT);
        boolean isOnline = NetworkUtils.isConnected(context);

        if (useCustomTabs && isOnline) {
            // Open in Custom tabs.
            helper.openCustomTab(Uri.parse(article.getLink()));
        } else {
            FullArticleActivity.launchWithAnimation(helper.getActivity(), image, "hero", article);
        }
    }
}