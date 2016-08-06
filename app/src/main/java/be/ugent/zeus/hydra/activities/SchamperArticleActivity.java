package be.ugent.zeus.hydra.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarActivity;
import be.ugent.zeus.hydra.models.schamper.Article;
import be.ugent.zeus.hydra.recyclerview.adapters.SchamperImageAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.html.PicassoImageGetter;
import be.ugent.zeus.hydra.utils.html.Utils;
import be.ugent.zeus.hydra.utils.recycler.SpacingItemDecoration;
import com.squareup.picasso.Picasso;

public class SchamperArticleActivity extends ToolbarActivity {

    public static final String PARCEL_ARTICLE = "article";

    private Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Hydra_Main_NoActionBar_SystemWindows);
        setContentView(R.layout.activity_schamper_article);

        customFade();

        Intent intent = getIntent();
        article = intent.getParcelableExtra(PARCEL_ARTICLE);

        TextView title = $(R.id.title);
        TextView date = $(R.id.date);
        TextView text = $(R.id.text);
        TextView intro = $(R.id.intro);
        TextView author = $(R.id.author);

        RecyclerView imageGrid = $(R.id.image_grid);
        SchamperImageAdapter adapter = new SchamperImageAdapter();
        imageGrid.setAdapter(adapter);
        imageGrid.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        int spacing = (int) getResources().getDimension(R.dimen.content_spacing);
        imageGrid.addItemDecoration(new SpacingItemDecoration(spacing));

        ImageView headerImage = $(R.id.header_image);

        if(article.getImage() != null) {
            Picasso.with(this).load(article.getImage().replace("/regulier/", "/preview/")).into(headerImage);
        }

        if(article.getAuthor() != null ) {
            author.setText(article.getAuthor());
        }

        if(article.getPubDate() != null) {
            date.setText(DateUtils.relativeDateString(article.getPubDate(), date.getContext()));
        }

        if(article.getBody() != null) {

            //The intro
            intro.setText(Utils.fromHtml(article.getIntro(), new PicassoImageGetter(intro, getResources(), this)));
            intro.setMovementMethod(LinkMovementMethod.getInstance());

            //Make a list of images
            //Add the images.
            adapter.setItems(article.getImages());

            //The body
            text.setText(Utils.fromHtml(article.getBody(), new PicassoImageGetter(text, getResources(), this)));
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if(article.getTitle() != null) {
            title.setText(article.getTitle());
            getSupportActionBar().setTitle(article.getTitle());
        }
    }

    @Override
    protected void sendScreen(HydraApplication application) {
        application.sendScreenName("Schamper article > " + article.getTitle());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_schamper, menu);

        setWhiteIcons(menu, R.id.schamper_share, R.id.schamper_browser);

        return true;
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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(article.getLink())));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}