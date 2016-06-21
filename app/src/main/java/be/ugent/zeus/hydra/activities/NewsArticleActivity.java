package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.association.NewsItem;
import be.ugent.zeus.hydra.recyclerview.viewholder.home.NewsItemViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.PicassoImageGetter;

public class NewsArticleActivity extends ToolbarActivity {

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_news_article);
        super.onCreate(savedInstanceState);

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
            text.setText(Html.fromHtml(article.getContent(), new PicassoImageGetter(text, getResources(), this), null));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if(article.getTitle() != null) {
            title.setText(article.getTitle());
            this.title = article.getTitle();
        }

        if(article.isHighlighted()) {
            title.setCompoundDrawables(null, null, getDrawable(R.drawable.star), null);
        }
    }

    @Override
    protected void sendScreen(HydraApplication application) {
        application.sendScreenName("News article > " + title);
    }
}