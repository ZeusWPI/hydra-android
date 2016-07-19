package be.ugent.zeus.hydra.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.html.PicassoImageGetter;
import be.ugent.zeus.hydra.utils.html.Utils;
import com.squareup.picasso.Picasso;

public class SchamperArticleActivity extends ToolbarActivity {

    public static final String PARCEL_ARTICLE = "article";

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Hydra_Main_NoActionBar_SystemWindows);
        setContentView(R.layout.activity_schamper_article);

        customFade();

        Intent intent = getIntent();
        Article article = intent.getParcelableExtra(PARCEL_ARTICLE);

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
            text.setText(Utils.fromHtml(article.getText(), new PicassoImageGetter(text, getResources(), this)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if(article.getTitle() != null) {
            title.setText(article.getTitle());
            this.title = article.getTitle();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void sendScreen(HydraApplication application) {
        application.sendScreenName("Schamper article > " + title);
    }

    /**
     * Launch this activity with a transition.
     *
     * @param activity The activity that launches the intent.
     * @param view The view to transition.
     * @param name The name of the transition.
     * @param article The article.
     */
    public static void launchWithAnimation(Activity activity, View view, String name, Parcelable article) {
        Intent intent = new Intent(activity, SchamperArticleActivity.class);
        intent.putExtra(PARCEL_ARTICLE, article);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, view, name);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }
}