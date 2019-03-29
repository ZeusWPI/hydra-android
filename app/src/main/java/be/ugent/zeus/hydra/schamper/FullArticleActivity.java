package be.ugent.zeus.hydra.schamper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.html.PicassoImageGetter;
import be.ugent.zeus.hydra.common.ui.html.Utils;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.utils.StringUtils;
import com.squareup.picasso.Picasso;

/**
 * Displays a full article.
 *
 * @author Niko Strijbol
 */
public class FullArticleActivity extends BaseActivity {

    private static final String PARCEL_ARTICLE = "article";

    private Article article;

    static void start(Context context, Article article) {
        Intent starter = new Intent(context, FullArticleActivity.class);
        starter.putExtra(PARCEL_ARTICLE, (Parcelable) article);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schamper_article);

        Intent intent = getIntent();
        article = intent.getParcelableExtra(PARCEL_ARTICLE);

        // Log viewing the article.
        Reporting.getTracker(this)
                .log(new ArticleShownEvent(article));

        TextView title = findViewById(R.id.title);
        TextView date = findViewById(R.id.date);
        TextView text = findViewById(R.id.text);
        TextView intro = findViewById(R.id.intro);
        TextView author = findViewById(R.id.author);


        ImageView headerImage = findViewById(R.id.header_image);

        if (article.getImage() != null) {
            Picasso.get().load(article.getImage()).into(headerImage);
        }

        if (article.getAuthor() != null) {
            author.setText(article.getAuthor());
        }

        String category = StringUtils.capitaliseFirst(article.getCategory());
        if (article.getPubDate() != null) {
            CharSequence dateString = DateUtils.relativeDateTimeString(article.getPubDate(), date.getContext());
            date.setText(getString(R.string.schamper_article_date_and_category, dateString, category));
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
            requireToolbar().setTitle(article.getTitle());
        }
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

    private static class ArticleShownEvent implements Event {

        private final Article article;

        ArticleShownEvent(Article article) {
            this.article = article;
        }

        @Nullable
        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Reporting.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.itemCategory(), Article.class.getSimpleName());
            params.putString(names.itemId(), article.getLink());
            params.putString(names.itemName(), article.getTitle());
            return params;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().viewItem();
        }
    }
}
