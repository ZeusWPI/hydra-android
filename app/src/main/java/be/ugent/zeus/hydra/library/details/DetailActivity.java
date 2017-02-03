package be.ugent.zeus.hydra.library.details;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.HydraActivity;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.list.LibraryListFragment;
import be.ugent.zeus.hydra.plugins.RequestPlugin;
import be.ugent.zeus.hydra.plugins.common.Plugin;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.utils.PreferencesUtils;
import be.ugent.zeus.hydra.utils.ViewUtils;
import com.squareup.picasso.Picasso;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Activity to display information about one {@link Library}.
 *
 * @author Niko Strijbol
 */
public class DetailActivity extends HydraActivity {

    private static final String ARG_LIBRARY = "argLibrary";

    private Library library;
    private Button button;
    private FrameLayout layout;

    // Due to the lambda, library should be not null when this is called.
    private RequestPlugin<OpeningHourList> plugin = new RequestPlugin<>((c, b) -> new OpeningHoursRequest(library));

    public static void launchActivity(Context context, Library library) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(ARG_LIBRARY, (Parcelable) library);
        context.startActivity(intent);
    }

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugin.hasProgress()
                .defaultError()
                .setDataCallback(this::receiveData);
        plugins.add(plugin);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_details);

        library = getIntent().getParcelableExtra(ARG_LIBRARY);

        layout = $(R.id.frame_layout);

        if (!TextUtils.isEmpty(library.getEnsuredImage())) {
            ImageView header = $(R.id.header_image);
            Picasso.with(this).load(library.getEnsuredImage()).into(header);
        } else {
            View header = $(R.id.header_container);
            header.setVisibility(View.GONE);
        }

        getToolbar().setTitle(library.getName());

        String address = makeFullAddressText();
        View container = $(R.id.library_address_card);
        if (TextUtils.isEmpty(address)) {
            container.setVisibility(View.GONE);
        } else {
            TextView textView = $(R.id.library_address);
            textView.setText(makeFullAddressText());
            container.setOnClickListener(v -> NetworkUtils.maybeLaunchIntent(DetailActivity.this, mapsIntent()));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> favourites = preferences.getStringSet(LibraryListFragment.PREF_LIBRARY_FAVOURITES, Collections.emptySet());

        button = $(R.id.library_favourite);
        Drawable[] drawables = button.getCompoundDrawables();
        Drawable drawable = drawables[0];
        if (favourites.contains(library.getCode())) {
            button.setSelected(true);
            DrawableCompat.setTint(drawable, ActivityCompat.getColor(this, R.color.ugent_yellow_dark));
        } else {
            DrawableCompat.setTint(drawable, ActivityCompat.getColor(this, R.color.ugent_blue_dark));
            button.setSelected(false);
        }

        // Save/remove libraries on button click
        button.setOnClickListener(v -> {
            if (button.isSelected()) {
                PreferencesUtils.removeFromStringSet(DetailActivity.this, LibraryListFragment.PREF_LIBRARY_FAVOURITES, library.getCode());
                DrawableCompat.setTint(drawable, ActivityCompat.getColor(DetailActivity.this, R.color.ugent_blue_dark));
                button.setSelected(false);
            } else {
                PreferencesUtils.addToStringSet(DetailActivity.this, LibraryListFragment.PREF_LIBRARY_FAVOURITES, library.getCode());
                DrawableCompat.setTint(drawable, ActivityCompat.getColor(DetailActivity.this, R.color.ugent_yellow_dark));
                button.setSelected(true);
            }
        });

        plugin.startLoader();
    }

    /**
     * Generate a table containing the opening hours and add it to the views.
     *
     * @param list The list of opening hours.
     */
    private void receiveData(OpeningHourList list) {

        final int rowPadding = ViewUtils.convertDpToPixelInt(4, this);

        TableLayout tableLayout = new TableLayout(this);

        TableRow header = new TableRow(this);
        TextView dateHeader = new TextView(this);
        dateHeader.setTypeface(null, Typeface.BOLD);
        dateHeader.setText(R.string.library_opening_header_date);
        TextView hoursHeader = new TextView(this);
        hoursHeader.setTypeface(null, Typeface.BOLD);
        hoursHeader.setText(R.string.library_opening_header_hours);
        header.addView(dateHeader);
        header.addView(hoursHeader);
        tableLayout.addView(header);

        for (OpeningHours hours: list) {
            TableRow tableRow = new TableRow(this);
            tableRow.setPadding(0, rowPadding, 0, rowPadding);
            TextView date = new TextView(this);
            date.setText(DateUtils.getFriendlyDate(hours.getDate()));
            TextView openHours = new TextView(this);
            openHours.setPadding(rowPadding, 0, 0, 0);
            openHours.setText(hours.getHours());
            tableRow.addView(date);
            tableRow.addView(openHours);
            tableLayout.addView(tableRow);
        }

        layout.addView(tableLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_library, menu);
        tintToolbarIcons(menu, R.id.library_location, R.id.library_email, R.id.library_phone);
        if (!library.hasTelephone()) {
            menu.removeItem(R.id.library_phone);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.library_location:
                NetworkUtils.maybeLaunchIntent(this, mapsIntent());
                return true;
            case R.id.library_email:
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("mailto:"));
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{library.getEmail()});
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, library.getName());
                NetworkUtils.maybeLaunchIntent(this, sendIntent);
                return true;
            case R.id.library_phone:
                if (library.hasTelephone()) {
                    Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                    phoneIntent.setData(Uri.fromParts("tel", library.getTelephone().get(0), null));
                    NetworkUtils.maybeLaunchIntent(this, phoneIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Intent mapsIntent() {
        Uri uri = Uri.parse("geo:" + library.getLatitude() + "," + library.getLongitude() + "0?q=" + library.addressAsString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        return intent;
    }

    private String makeFullAddressText() {
        List<String> parts = new ArrayList<>();
        parts.add(library.getName());
        if (!TextUtils.isEmpty(library.getCampus())) {
            String campus = library.getCampus();
            if (!TextUtils.isEmpty(library.getDepartement())) {
                campus += " (" + library.getDepartement() + ")";
            }
            parts.add(campus);
        }
        parts.addAll(library.getAddress());
        return StreamSupport.stream(parts).collect(Collectors.joining("\n"));
    }
}