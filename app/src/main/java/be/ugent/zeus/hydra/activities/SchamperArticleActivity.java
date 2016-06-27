package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.PicassoImageGetter;
import com.squareup.picasso.Picasso;

import static android.text.Html.FROM_HTML_MODE_LEGACY;

public class SchamperArticleActivity extends ToolbarActivity {

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Hydra_Main_NoActionBar_SystemWindows);
        setContentView(R.layout.activity_schamper_article);

        Intent intent = getIntent();
        Article article = intent.getParcelableExtra("article");

        TextView title = $(R.id.title);
        TextView date = $(R.id.date);
        TextView text = $(R.id.text);
        TextView author = $(R.id.author);

        ImageView headerImage = $(R.id.header_image);

        CollapsingToolbarLayout collapsingToolbarLayout = $(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.alpha(0));

        if(article.getImage() != null) {
            Picasso.with(this).load(article.getImage().replace("/regulier/", "/preview/")).into(headerImage);
        }

        if(article.getAuthor() != null ) {
            author.setText(article.getAuthor());
        }

        if(article.getPubDate() != null) {
            date.setText(DateUtils.relativeDateString(article.getPubDate(), date.getContext()));
        }

        if(article.getText() != null) {
            setText(text, article.getText());
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if(article.getTitle() != null) {
            title.setText(article.getTitle());
            this.title = article.getTitle();
        }
    }

    @Override
    protected void sendScreen(HydraApplication application) {
        application.sendScreenName("Schamper article > " + title);
    }

    private void setText(TextView text, String string) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            text.setText(Html.fromHtml(string, FROM_HTML_MODE_LEGACY, new PicassoImageGetter(text, getResources(), this), null));
        } else {
            //noinspection deprecation
            text.setText(Html.fromHtml(string, new PicassoImageGetter(text, getResources(), this), null));
        }
    }
}