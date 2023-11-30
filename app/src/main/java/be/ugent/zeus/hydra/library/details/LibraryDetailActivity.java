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

package be.ugent.zeus.hydra.library.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.IntentCompat;
import androidx.core.text.util.LinkifyCompat;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.common.reporting.BaseEvents;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.html.Utils;
import be.ugent.zeus.hydra.common.utils.*;
import be.ugent.zeus.hydra.databinding.ActivityLibraryDetailsBinding;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.favourites.LibraryFavourite;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import net.cachapa.expandablelayout.ExpandableLayout;

/**
 * Activity to display information about one {@link Library}.
 *
 * @author Niko Strijbol
 */
public class LibraryDetailActivity extends BaseActivity<ActivityLibraryDetailsBinding> {

    public static final String ARG_LIBRARY = "argLibrary";

    private static final String TAG = "LibraryDetailActivity";

    private Library library;

    public static void launchActivity(Context context, Library library) {
        Intent intent = new Intent(context, LibraryDetailActivity.class);
        intent.putExtra(ARG_LIBRARY, library);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityLibraryDetailsBinding::inflate);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        library = IntentCompat.getParcelableExtra(getIntent(), ARG_LIBRARY, Library.class);

        Picasso.get().load(library.headerImage(this)).into(binding.headerImage);

        binding.collapsingToolbar.setTitle(library.name());
        String address = makeFullAddressText();
        if (TextUtils.isEmpty(address)) {
            binding.libraryAddressCard.setVisibility(View.GONE);
        } else {
            binding.libraryAddress.setText(makeFullAddressText());
            binding.libraryAddress.setOnClickListener(v -> NetworkUtils.maybeLaunchIntent(LibraryDetailActivity.this, mapsIntent()));
        }

        final ViewModelProvider provider = new ViewModelProvider(this);

        FavouriteViewModel viewModel = provider.get(FavouriteViewModel.class);
        viewModel.setLibrary(library);
        viewModel.getData().observe(this, isFavourite -> {
            Drawable drawable;
            Context c = LibraryDetailActivity.this;
            if (isFavourite) {
                binding.libraryFavourite.setSelected(true);
                drawable = ViewUtils.getTintedVectorDrawableAttr(c, R.drawable.ic_star, R.attr.colorSecondary);
            } else {
                drawable = ViewUtils.getTintedVectorDrawableAttr(c, R.drawable.ic_star, R.attr.colorPrimary);
                binding.libraryFavourite.setSelected(false);
            }
            binding.libraryFavourite.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        });
        binding.libraryFavourite.setOnClickListener(v -> updateStatus(library, binding.libraryFavourite.isSelected()));

        binding.expandButton.setOnClickListener(v -> binding.expandableLayout.toggle());

        binding.expandableLayout.setOnExpansionUpdateListener((value, state) -> {
            if (state == ExpandableLayout.State.COLLAPSED) {
                binding.expandButton.setText(R.string.library_more);
            } else if (state == ExpandableLayout.State.EXPANDED) {
                binding.expandButton.setText(R.string.library_less);
            }
        });

        String comments = library.commentsAsString();
        if (TextUtils.isEmpty(comments)) {
            binding.libraryRemarks.setVisibility(View.GONE);
            binding.libraryRemarksDivider.setVisibility(View.GONE);
            binding.libraryRemarksTitle.setVisibility(View.GONE);
        } else {
            binding.libraryRemarks.setText(Utils.fromHtml(comments));
            binding.expandableLayout.setExpanded(true, false);
        }

        binding.libraryMailRowText.setText(library.email());
        LinkifyCompat.addLinks(binding.libraryMailRowText, Linkify.EMAIL_ADDRESSES);
        String phoneString = library.getPhones();
        if (TextUtils.isEmpty(phoneString)) {
            binding.libraryPhoneRowText.setText(R.string.library_detail_no_phone);
        } else {
            binding.libraryPhoneRowText.setText(phoneString);
            LinkifyCompat.addLinks(binding.libraryPhoneRowText, Linkify.PHONE_NUMBERS);
        }
        binding.libraryContactRowText.setText(library.contact());

        HoursViewModel model = provider.get(HoursViewModel.class);
        model.setLibrary(library);
        model.data().observe(this, PartialErrorObserver.with(this::onError));
        model.data().observe(this, new ProgressObserver<>(binding.progressBar));
        model.data().observe(this, SuccessObserver.with(this::receiveData));

        Reporting.getTracker(this).log(new LibraryViewEvent(library));
    }

    private void updateStatus(Library library, boolean isSelected) {
        var repository = Database.get(this).getFavouriteRepository();
        ThreadingUtils.execute(() -> {
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
        outState.putString("button", String.valueOf(binding.expandButton.getText()));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the toggle button state.
        if (savedInstanceState.getString("button") != null) {
            binding.expandButton.setText(savedInstanceState.getString("button"));
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
            TextView date = new TextView(this, null, R.attr.textAppearanceBodyMedium);
            date.setText(DateUtils.friendlyDate(this, hours.date()));
            TextView openHours = new TextView(this, null, R.attr.textAppearanceBodyMedium);
            openHours.setPadding(rowPadding, 0, 0, 0);
            openHours.setText(hours.hours());
            tableRow.addView(date);
            tableRow.addView(openHours);
            if (!TextUtils.isEmpty(hours.comments())) {
                TextView comments = new TextView(this, null, R.attr.textAppearanceBodyMedium);
                TableLayout.LayoutParams params = new TableLayout.LayoutParams();
                params.weight = 0;
                params.width = 0;
                params.height = TableLayout.LayoutParams.MATCH_PARENT;
                comments.setLayoutParams(params);
                comments.setPadding(rowPadding, 0, 0, 0);
                comments.setText(hours.comments());
            }
            tableLayout.addView(tableRow);
        }

        binding.frameLayout.addView(tableLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_library_details, menu);
        tintToolbarIcons(menu, R.id.library_url);
        if (library.link() == null) {
            menu.removeItem(R.id.library_url);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.library_url) {
            if (library.link() != null) {
                NetworkUtils.maybeLaunchBrowser(this, library.link());
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @return Intent to launch Google Maps to show the location of the library.
     */
    private Intent mapsIntent() {
        Uri uri = Uri.parse("geo:" + library.latitude() + "," + library.longitude() + "0?q=" + library.addressAsString());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        return intent;
    }

    /**
     * @return A generated address, containing the campus and faculty (and department) in addition to the address.
     */
    private String makeFullAddressText() {
        List<String> parts = new ArrayList<>();
        parts.add(library.name());
        if (!TextUtils.isEmpty(library.campus())) {
            String campus = library.campus();
            if (!TextUtils.isEmpty(library.faculty())) {
                campus += " (" + library.faculty();
                if (!TextUtils.isEmpty(library.department())) {
                    campus += ", " + library.department();
                }
            }
            campus += ")";
            parts.add(campus);
        }
        parts.addAll(library.address());
        //noinspection SimplifyStreamApiCallChains
        return parts.stream().collect(Collectors.joining("\n"));
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .show();
    }

    private record LibraryViewEvent(Library library) implements Event {
        
        @Override
        public Bundle params() {
            BaseEvents.Params names = Reporting.getEvents().params();
            Bundle params = new Bundle();
            params.putString(names.itemCategory(), Library.class.getSimpleName());
            params.putString(names.itemId(), library.code());
            params.putString(names.itemName(), library.name());
            return params;
        }

        @Nullable
        @Override
        public String eventName() {
            return Reporting.getEvents().viewItem();
        }
    }
}
