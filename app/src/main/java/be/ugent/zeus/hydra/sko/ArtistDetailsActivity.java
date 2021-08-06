/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.sko;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.databinding.ActivitySkoArtistBinding;
import com.squareup.picasso.Picasso;

/**
 * Display information for a specific {@link Artist}.
 *
 * @author Niko Strijbol
 */
public class ArtistDetailsActivity extends BaseActivity<ActivitySkoArtistBinding> {

    private static final String PARCEL_ARTIST = "artist";

    private Artist artist;

    @NonNull
    public static Intent start(@NonNull Context context, @NonNull Artist artist) {
        Intent intent = new Intent(context, ArtistDetailsActivity.class);
        intent.putExtra(PARCEL_ARTIST, artist);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivitySkoArtistBinding::inflate);

        Intent intent = getIntent();
        artist = intent.getParcelableExtra(PARCEL_ARTIST);
        assert artist != null;

        binding.title.setText(artist.getName());
        setTitle(artist.getName());

        if (artist.getImage() != null) {
            Picasso.get().load(artist.getImage()).fit().centerInside().into(binding.headerImage);
        }

        binding.date.setText(artist.getDisplayDate(this));

        if (!TextUtils.isEmpty(artist.getDescription())) {
            binding.content.setText(artist.getDescription());
        } else {
            binding.content.setText(R.string.sko_artist_no_content);
        }

        binding.skoArtistSearchWeb.setOnClickListener(view -> {
            Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
            searchIntent.putExtra(SearchManager.QUERY, artist.getName());
            NetworkUtils.maybeLaunchIntent(ArtistDetailsActivity.this, searchIntent);
        });

        binding.skoArtistSearchMusic.setOnClickListener(view -> {
            Intent musicIntent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
            musicIntent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
            musicIntent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist.getName());
            musicIntent.putExtra(SearchManager.QUERY, artist.getName());
            NetworkUtils.maybeLaunchIntent(ArtistDetailsActivity.this, musicIntent);
        });

        Reporting.getTracker(this).log(new ArtistViewedEvent(artist));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sko_artist, menu);
        tintToolbarIcons(menu, R.id.sko_add_to_calendar);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sko_add_to_calendar) {
            startActivity(artist.addToCalendarIntent());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class ArtistViewedEvent implements Event {

        private final Artist artist;

        private ArtistViewedEvent(Artist artist) {
            this.artist = artist;
        }

        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Reporting.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.itemCategory(), Artist.class.getSimpleName());
            params.putString(names.itemId(), artist.getName());
            params.putString(names.itemName(), artist.getName());
            return params;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().viewItem();
        }
    }
}
