package be.ugent.zeus.hydra.sko.lineup;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.utils.Analytics;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

/**
 * Display information for a specific {@link Artist}.
 *
 * @author Niko Strijbol
 */
public class ArtistDetailsActivity extends BaseActivity {

    private static final String PARCEL_ARTIST = "artist";

    private Artist artist;

    public static Intent start(Context context, Artist artist) {
        Intent intent = new Intent(context, ArtistDetailsActivity.class);
        intent.putExtra(PARCEL_ARTIST, (Parcelable) artist);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sko_artist);

        Intent intent = getIntent();
        artist = intent.getParcelableExtra(PARCEL_ARTIST);

        TextView title = findViewById(R.id.title);
        TextView date = findViewById(R.id.date);
        TextView content = findViewById(R.id.content);
        ImageView headerImage = findViewById(R.id.header_image);

        title.setText(artist.getName());

        if (artist.getImage() != null) {
            Picasso.get().load(artist.getImage()).fit().centerInside().into(headerImage);
        }

        date.setText(artist.getDisplayDate(this));

        if (!TextUtils.isEmpty(artist.getContent())) {
            content.setText(artist.getContent());
        } else {
            content.setText(R.string.sko_artist_no_content);
        }

        findViewById(R.id.sko_artist_search_web).setOnClickListener(view -> {
            Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
            searchIntent.putExtra(SearchManager.QUERY, artist.getName());
            NetworkUtils.maybeLaunchIntent(ArtistDetailsActivity.this, searchIntent);
        });

        findViewById(R.id.sko_artist_search_music).setOnClickListener(view -> {
            Intent musicIntent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
            musicIntent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
            musicIntent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist.getName());
            musicIntent.putExtra(SearchManager.QUERY, artist.getName());
            NetworkUtils.maybeLaunchIntent(ArtistDetailsActivity.this, musicIntent);
        });

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle parameters = new Bundle();
        parameters.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, Analytics.Type.SKO_ARTIST);
        parameters.putString(FirebaseAnalytics.Param.ITEM_NAME, artist.getName());
        parameters.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(artist.hashCode()));
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, parameters);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sko_artist, menu);
        tintToolbarIcons(menu, R.id.sko_add_to_calendar);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Share button
            case R.id.sko_add_to_calendar:
                startActivity(artist.addToCalendarIntent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}