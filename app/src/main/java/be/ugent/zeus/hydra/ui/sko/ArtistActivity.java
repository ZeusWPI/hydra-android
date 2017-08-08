package be.ugent.zeus.hydra.ui.sko;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.sko.Artist;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.sko.overview.LineupViewHolder;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Display information for a specific {@link Artist}.
 *
 * @author Niko Strijbol
 */
public class ArtistActivity extends BaseActivity {

    public static final String PARCEL_ARTIST = "artist";

    private Artist artist;

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
            Picasso.with(this).load(artist.getImage()).fit().centerInside().into(headerImage);
        }

        date.setText(LineupViewHolder.getDisplayDate(artist));

        if (!TextUtils.isEmpty(artist.getContent())) {
            content.setText(artist.getContent());
        } else {
            content.setText(R.string.sko_artist_no_content);
        }

        findViewById(R.id.sko_artist_search_web).setOnClickListener(view -> {
            Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
            searchIntent.putExtra(SearchManager.QUERY, artist.getName());
            NetworkUtils.maybeLaunchIntent(ArtistActivity.this, searchIntent);
        });

        findViewById(R.id.sko_artist_search_music).setOnClickListener(view -> {
            Intent musicIntent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
            musicIntent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
            musicIntent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist.getName());
            musicIntent.putExtra(SearchManager.QUERY, artist.getName());
            NetworkUtils.maybeLaunchIntent(ArtistActivity.this, musicIntent);
        });
    }

    @Override
    protected String getScreenName() {
        return "SKO artist > " + artist.getName();
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
                LineupViewHolder.addToCalendar(this, artist);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}