package be.ugent.zeus.hydra.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.ui.common.customtabs.CustomTabsHelper;
import be.ugent.zeus.hydra.ui.common.html.PicassoImageGetter;
import be.ugent.zeus.hydra.ui.common.html.Utils;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 * Display a news article from DSA.
 *
 * @author Niko Strijbol
 */
public class NewsArticleActivity extends BaseActivity {

    public static final String PARCEL_NAME = "newsItem";

    private ActivityHelper helper;

    private String title;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_article);

        Intent intent = getIntent();
        UgentNewsItem article = intent.getParcelableExtra(PARCEL_NAME);

        this.url = article.getIdentifier();

        TextView title = findViewById(R.id.title);
        TextView date = findViewById(R.id.date);
        TextView text = findViewById(R.id.text);
        TextView author = findViewById(R.id.author);
        TextView lead = findViewById(R.id.article_lead);

        author.setText(TextUtils.join(", ", article.getCreators()));

        if (article.getCreated() != null) {
            date.setText(DateUtils.relativeDateTimeString(article.getCreated(), date.getContext()));
        }

        if (!TextUtils.isEmpty(article.getDescription())) {
            lead.setText(Utils.fromHtml(article.getDescription(), new PicassoImageGetter(lead, getResources(), this)));
        } else {
            lead.setVisibility(View.GONE);
        }

        if (article.getText() != null) {
            text.setText(Utils.fromHtml(article.getText(), new PicassoImageGetter(text, getResources(), this)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (article.getTitle() != null) {
            title.setText(article.getTitle());
            this.title = article.getTitle();
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
        switch (item.getItemId()) {
            case R.id.article_link:
                helper.openCustomTab(Uri.parse(url));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    protected String getScreenName() {
        return "News article > " + title;
    }
}