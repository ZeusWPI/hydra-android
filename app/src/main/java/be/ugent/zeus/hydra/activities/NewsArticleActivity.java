package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.recyclerview.viewholder.home.NewsItemViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.html.PicassoImageGetter;
import be.ugent.zeus.hydra.utils.html.Utils;

public class NewsArticleActivity extends ToolbarActivity {

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_article);

        Intent intent = getIntent();
        NewsItem article = intent.getParcelableExtra(NewsItemViewHolder.PARCEL_NAME);

        TextView title = $(R.id.title);
        TextView date = $(R.id.date);
        TextView text = $(R.id.text);
        TextView author = $(R.id.author);

        if(article.getAssociation() != null ) {
            author.setText(article.getAssociation().getDisplayName());
        }

        if(article.getDate() != null) {
            date.setText(DateUtils.relativeDateString(article.getDate(), date.getContext()));
        }

        if(article.getContent() != null) {
            text.setText(Utils.fromHtml(article.getContent(), new PicassoImageGetter(text, getResources(), this)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if(article.getTitle() != null) {
            title.setText(article.getTitle());
            this.title = article.getTitle();
        }

        if(article.isHighlighted()) {
            title.setCompoundDrawables(null, null, ContextCompat.getDrawable(this, R.drawable.star), null);
        }
    }

    @Override
    protected void sendScreen(HydraApplication application) {
        application.sendScreenName("News article > " + title);
    }
}