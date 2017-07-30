package be.ugent.zeus.hydra.ui.library;

import android.arch.lifecycle.ViewModelProviders;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.text.util.LinkifyCompat;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.data.models.library.OpeningHours;
import be.ugent.zeus.hydra.repository.observers.ErrorObserver;
import be.ugent.zeus.hydra.repository.observers.ProgressObserver;
import be.ugent.zeus.hydra.repository.observers.SuccessObserver;
import be.ugent.zeus.hydra.ui.common.BaseActivity;
import be.ugent.zeus.hydra.ui.common.ViewUtils;
import be.ugent.zeus.hydra.ui.common.html.Utils;
import be.ugent.zeus.hydra.ui.main.library.LibraryListFragment;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.NetworkUtils;
import be.ugent.zeus.hydra.utils.PreferencesUtils;
import com.squareup.picasso.Picasso;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Activity to display information about one {@link Library}.
 * <p>
 * TODO: investigate if the view hierarchy can be less deep.
 *
 * @author Niko Strijbol
 */
public class LibraryDetailActivity extends BaseActivity {

    private static final String ARG_LIBRARY = "argLibrary";

    private static final String TAG = "LibraryDetailActivity";

    private Library library;
    private Button button;
    private Button expandButton;
    private FrameLayout layout;

    public static void launchActivity(Context context, Library library) {
        Intent intent = new Intent(context, LibraryDetailActivity.class);
        intent.putExtra(ARG_LIBRARY, (Parcelable) library);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_details);

        library = getIntent().getParcelableExtra(ARG_LIBRARY);
        layout = findViewById(R.id.frame_layout);

        ImageView header = findViewById(R.id.header_image);
        Picasso.with(this).load(library.getEnsuredImage()).into(header);

        getToolbar().setTitle(library.getName());

        String address = makeFullAddressText();
        if (TextUtils.isEmpty(address)) {
            findViewById(R.id.library_address_card).setVisibility(View.GONE);
        } else {
            TextView textView = findViewById(R.id.library_address);
            textView.setText(makeFullAddressText());
            textView.setOnClickListener(v -> NetworkUtils.maybeLaunchIntent(LibraryDetailActivity.this, mapsIntent()));
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> favourites = preferences.getStringSet(LibraryListFragment.PREF_LIBRARY_FAVOURITES, Collections.emptySet());

        button = findViewById(R.id.library_favourite);
        // Set compound drawable in code, for backwards comparability.
        Drawable drawable;
        if (favourites.contains(library.getCode())) {
            button.setSelected(true);
            drawable = ViewUtils.getTintedVectorDrawable(this, R.drawable.ic_star, R.color.ugent_yellow_dark);
        } else {
            drawable = ViewUtils.getTintedVectorDrawable(this, R.drawable.ic_star, R.color.ugent_blue_dark);
            button.setSelected(false);
        }

        button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        // Save/remove libraries on button click
        button.setOnClickListener(v -> {
            if (button.isSelected()) {
                PreferencesUtils.removeFromStringSet(LibraryDetailActivity.this, LibraryListFragment.PREF_LIBRARY_FAVOURITES, library.getCode());
                DrawableCompat.setTint(drawable, ActivityCompat.getColor(LibraryDetailActivity.this, R.color.ugent_blue_dark));
                button.setSelected(false);
            } else {
                PreferencesUtils.addToStringSet(LibraryDetailActivity.this, LibraryListFragment.PREF_LIBRARY_FAVOURITES, library.getCode());
                DrawableCompat.setTint(drawable, ActivityCompat.getColor(LibraryDetailActivity.this, R.color.ugent_yellow_dark));
                button.setSelected(true);
            }
        });

        ExpandableLayout layout = findViewById(R.id.expandable_layout);
        expandButton = findViewById(R.id.expand_button);
        expandButton.setOnClickListener(v -> layout.toggle());

        layout.setOnExpansionUpdateListener(f -> {
            if (f == 0) {
                expandButton.setText(R.string.library_more);
            } else if (f == 1) {
                expandButton.setText(R.string.library_less);
            }
        });

        TextView remarks = findViewById(R.id.library_remarks);
        String comments = library.getCommentsAsString();
        if (TextUtils.isEmpty(comments)) {
            remarks.setVisibility(View.GONE);
            findViewById(R.id.library_remarks_divider).setVisibility(View.GONE);
            findViewById(R.id.library_remarks_title).setVisibility(View.GONE);
        } else {
            remarks.setText(Utils.fromHtml(comments));
        }

        TextView email = findViewById(R.id.library_mail_row_text);
        email.setText(library.getEmail());
        LinkifyCompat.addLinks(email, Linkify.EMAIL_ADDRESSES);
        TextView phone = findViewById(R.id.library_phone_row_text);
        String phoneString = library.getPhones();
        if (TextUtils.isEmpty(phoneString)) {
            phone.setText(R.string.library_no_phone);
        } else {
            phone.setText(phoneString);
            LinkifyCompat.addLinks(phone, Linkify.PHONE_NUMBERS);
        }
        TextView contact = findViewById(R.id.library_contact_row_text);
        contact.setText(library.getContact());

        HoursViewModel model = ViewModelProviders.of(this).get(HoursViewModel.class);
        model.setLibrary(library);
        model.getData().observe(this, ErrorObserver.with(this::onError));
        model.getData().observe(this, new ProgressObserver<>(findViewById(R.id.progress_bar)));
        model.getData().observe(this, SuccessObserver.with(this::receiveData));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the toggle button state.
        outState.putString("button", String.valueOf(expandButton.getText()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the toggle button state.
        if (savedInstanceState != null && savedInstanceState.get("button") != null) {
            expandButton.setText(savedInstanceState.getString("button"));
        }
    }

    /**
     * Generate a table containing the opening hours and insert it to the views.
     *
     * @param list The list of opening hours.
     */
    private void receiveData(List<OpeningHours> list) {

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

        for (OpeningHours hours : list) {
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
        getMenuInflater().inflate(R.menu.menu_library_details, menu);
        tintToolbarIcons(menu, R.id.library_location, R.id.library_email, R.id.library_phone, R.id.library_url);
        if (!library.hasTelephone()) {
            menu.removeItem(R.id.library_phone);
        }
        if (library.getLink() == null) {
            menu.removeItem(R.id.library_url);
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
            case R.id.library_url:
                if (library.getLink() != null) {
                    NetworkUtils.maybeLaunchBrowser(this, library.getLink());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * @return Intent to launch Google Maps to show the location of the library.
     */
    private Intent mapsIntent() {
        Uri uri = Uri.parse("geo:" + library.getLatitude() + "," + library.getLongitude() + "0?q=" + library.addressAsString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        return intent;
    }

    /**
     * @return A generated address, containing the campus and faculty (and department) in addition to the address.
     */
    private String makeFullAddressText() {
        List<String> parts = new ArrayList<>();
        parts.add(library.getName());
        if (!TextUtils.isEmpty(library.getCampus())) {
            String campus = library.getCampus();
            if (!TextUtils.isEmpty(library.getFaculty())) {
                campus += " (" + library.getFaculty();
                if (!TextUtils.isEmpty(library.getDepartment())) {
                    campus += ", " + library.getDepartment();
                }
            }
            campus += ")";
            parts.add(campus);
        }
        parts.addAll(library.getAddress());
        return StreamSupport.stream(parts).collect(Collectors.joining("\n"));
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.failure), Snackbar.LENGTH_LONG)
                .show();
    }
}