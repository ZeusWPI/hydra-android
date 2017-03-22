package be.ugent.zeus.hydra.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.html.PicassoImageGetter;
import be.ugent.zeus.hydra.utils.html.Utils;

/**
 * Display a news article from DSA.
 *
 * @author Niko Strijbol
 */
public class NewsArticleActivity extends BaseActivity {

    public static final String PARCEL_NAME = "newsItem";

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_article);

        Intent intent = getIntent();
        UgentNewsItem article = intent.getParcelableExtra(PARCEL_NAME);

        TextView title = $(R.id.title);
        TextView date = $(R.id.date);
        TextView text = $(R.id.text);
        TextView author = $(R.id.author);

        if (!article.getContributors().isEmpty()) {
            author.setText(TextUtils.join(", ", article.getContributors()));
        }

        if (article.getCreated() != null) {
            date.setText(DateUtils.relativeDateTimeString(article.getCreated(), date.getContext()));
        }

        if (article.getText() != null) {
            text.setText(Utils.fromHtml(article.getText(), new PicassoImageGetter(text, getResources(), this)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if (article.getTitle() != null) {
            title.setText(article.getTitle());
            this.title = article.getTitle();
        }
    }

    @Override
    protected String getScreenName() {
        return "News article > " + title;
    }
}