package be.ugent.zeus.hydra.library.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.text.util.LinkifyCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.utils.ViewUtils;
import be.ugent.zeus.hydra.common.ui.html.Utils;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.favourites.FavouritesRepository;
import be.ugent.zeus.hydra.library.favourites.LibraryFavourite;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import net.cachapa.expandablelayout.ExpandableLayout;

/**
 * Activity to display information about one {@link Library}.
 *
 * @author Niko Strijbol
 */
public class LibraryDetailActivity extends BaseActivity {

    public static final String ARG_LIBRARY = "argLibrary";

    private static final String TAG = "LibraryDetailActivity";

    private Library library;
    private Button button;
    private Button expandButton;
    private FrameLayout layout;

    public static void launchActivity(Context context, Library library) {
        Intent intent = new Intent(context, LibraryDetailActivity.class);
        intent.putExtra(ARG_LIBRARY, library);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_details);

        library = getIntent().getParcelableExtra(ARG_LIBRARY);
        layout = findViewById(R.id.frame_layout);

        ImageView header = findViewById(R.id.header_image);
        Picasso.get().load(library.getHeaderImage(this)).into(header);

        CollapsingToolbarLayout collapsingToolbarLayout = requireViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(library.getName());
        // TODO: why is this necessary?
        int white = ContextCompat.getColor(this, R.color.white);
        collapsingToolbarLayout.setExpandedTitleColor(white);
        collapsingToolbarLayout.setCollapsedTitleTextColor(white);

        String address = makeFullAddressText();
        if (TextUtils.isEmpty(address)) {
            findViewById(R.id.library_address_card).setVisibility(View.GONE);
        } else {
            TextView textView = findViewById(R.id.library_address);
            textView.setText(makeFullAddressText());
            textView.setOnClickListener(v -> NetworkUtils.maybeLaunchIntent(LibraryDetailActivity.this, mapsIntent()));
        }

        final ViewModelProvider provider = new ViewModelProvider(this);

        button = findViewById(R.id.library_favourite);
        FavouriteViewModel viewModel = provider.get(FavouriteViewModel.class);
        viewModel.setLibrary(library);
        viewModel.getData().observe(this, isFavourite -> {
            Drawable drawable;
            Context c = LibraryDetailActivity.this;
            if (isFavourite) {
                button.setSelected(true);
                drawable = ViewUtils.getTintedVectorDrawableAttr(c, R.drawable.ic_star, R.attr.colorSecondary);
            } else {
                drawable = ViewUtils.getTintedVectorDrawableAttr(c, R.drawable.ic_star, R.attr.colorPrimary);
                button.setSelected(false);
            }
            button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        });
        button.setOnClickListener(v -> updateStatus(library, button.isSelected()));

        ExpandableLayout layout = findViewById(R.id.expandable_layout);
        expandButton = findViewById(R.id.expand_button);
        expandButton.setOnClickListener(v -> layout.toggle());

        layout.setOnExpansionUpdateListener((value, state) -> {
            if (state == ExpandableLayout.State.COLLAPSED) {
                expandButton.setText(R.string.library_more);
            } else if (state == ExpandableLayout.State.EXPANDED) {
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
            layout.setExpanded(true, false);
        }

        TextView email = findViewById(R.id.library_mail_row_text);
        email.setText(library.getEmail());
        LinkifyCompat.addLinks(email, Linkify.EMAIL_ADDRESSES);
        TextView phone = findViewById(R.id.library_phone_row_text);
        String phoneString = library.getPhones();
        if (TextUtils.isEmpty(phoneString)) {
            phone.setText(R.string.library_detail_no_phone);
        } else {
            phone.setText(phoneString);
            LinkifyCompat.addLinks(phone, Linkify.PHONE_NUMBERS);
        }
        TextView contact = findViewById(R.id.library_contact_row_text);
        contact.setText(library.getContact());

        HoursViewModel model = provider.get(HoursViewModel.class);
        model.setLibrary(library);
        model.getData().observe(this, PartialErrorObserver.with(this::onError));
        model.getData().observe(this, new ProgressObserver<>(findViewById(R.id.progress_bar)));
        model.getData().observe(this, SuccessObserver.with(this::receiveData));

        Reporting.getTracker(this).log(new LibraryViewEvent(library));
    }

    private void updateStatus(Library library, boolean isSelected) {
        FavouritesRepository repository = Database.get(this).getFavouritesRepository();
        AsyncTask.execute(() -> {
            if (isSelected) {
                repository.delete(LibraryFavourite.from(library));
            } else {
                repository.insert(LibraryFavourite.from(library));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the toggle button state.
        outState.putString("button", String.valueOf(expandButton.getText()));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the toggle button state.
        if (savedInstanceState.get("button") != null) {
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

        for (OpeningHours hours : list) {
            TableRow tableRow = new TableRow(this);
            tableRow.setPadding(0, rowPadding, 0, rowPadding);
            TextView date = new TextView(this, null, R.attr.textAppearanceBody2);
            date.setText(DateUtils.getFriendlyDate(this, hours.getDate()));
            TextView openHours = new TextView(this, null, R.attr.textAppearanceBody2);
            openHours.setPadding(rowPadding, 0, 0, 0);
            openHours.setText(hours.getHours());
            tableRow.addView(date);
            tableRow.addView(openHours);
            if (!TextUtils.isEmpty(hours.getComments())) {
                TextView comments = new TextView(this, null, R.attr.textAppearanceBody2);
                TableLayout.LayoutParams params = new TableLayout.LayoutParams();
                params.weight = 0;
                params.width = 0;
                params.height = TableLayout.LayoutParams.MATCH_PARENT;
                comments.setLayoutParams(params);
                comments.setPadding(rowPadding, 0, 0, 0);
                comments.setText(hours.getComments());
            }
            tableLayout.addView(tableRow);
        }

        layout.addView(tableLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_library_details, menu);
        tintToolbarIcons(menu, R.id.library_location, R.id.library_email, R.id.library_url);
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
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .show();
    }

    private static class LibraryViewEvent implements Event {

        private final Library library;

        private LibraryViewEvent(Library library) {
            this.library = library;
        }

        @Nullable
        @Override
        public Bundle getParams() {
            BaseEvents.Params names = Reporting.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.itemCategory(), Library.class.getSimpleName());
            params.putString(names.itemId(), library.getCode());
            params.putString(names.itemName(), library.getName());
            return params;
        }

        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().viewItem();
        }
    }
}
