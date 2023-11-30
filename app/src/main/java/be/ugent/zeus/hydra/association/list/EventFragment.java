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

package be.ugent.zeus.hydra.association.list;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.AssociationRequest;
import be.ugent.zeus.hydra.association.EventFilter;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.ui.recyclerview.EmptyViewObserver;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import org.jetbrains.annotations.NotNull;

import static be.ugent.zeus.hydra.common.utils.FragmentUtils.requireBaseActivity;

/**
 * Displays a list of activities, filtered by the settings.
 *
 * @author ellen
 * @author Niko Strijbol
 */
public class EventFragment extends Fragment implements MainActivity.ScheduledRemovalListener, MainActivity.OnBackPressed {

    private static final String TAG = "EventFragment";

    private final EventAdapter adapter = new EventAdapter();
    private final EventFilter.Live filter = new EventFilter.Live();
    private final AssociationAdapter associationAdapter = new AssociationAdapter();
    private EventViewModel viewModel;
    private FrameLayout bottomSheet;
    private BottomSheetBehavior<FrameLayout> behavior;
    private Toolbar bottomToolbar;
    private TextInputLayout searchTerm;
    private TextInputLayout startTime;
    private TextInputLayout endTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_main_events, menu);
                requireBaseActivity(EventFragment.this).tintToolbarIcons(menu, R.id.action_refresh, R.id.action_search);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull @NotNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.action_refresh) {
                    viewModel.onRefresh();
                    return true;
                } else if (itemId == R.id.action_search) {
                    showSheet();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        bottomSheet = requireActivity().findViewById(R.id.bottom_sheet);
        // Load the options.
        getLayoutInflater().inflate(R.layout.item_search_filter, bottomSheet, true);
        behavior = BottomSheetBehavior.from(bottomSheet);
        RecyclerView associationRecycler = ViewCompat.requireViewById(bottomSheet, R.id.assoc_recycler_view);
        bottomToolbar = ViewCompat.requireViewById(bottomSheet, R.id.bottom_sheet_toolbar);
        bottomToolbar.inflateMenu(R.menu.menu_event_search);
        bottomToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                doFiltering();
                return true;
            }
            return false;
        });
        searchTerm = ViewCompat.requireViewById(bottomSheet, R.id.search_term);
        startTime = ViewCompat.requireViewById(bottomSheet, R.id.start_time);
        assert startTime.getEditText() != null;
        startTime.getEditText().setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Startend na...")
                    .build();
            picker.addOnPositiveButtonClickListener(selection -> {
                OffsetDateTime offsetDateTime = Instant.ofEpochMilli(selection).atOffset(ZoneOffset.UTC);
                filter.after(offsetDateTime);
            });
            picker.show(getChildFragmentManager(), picker.toString());
        });
        endTime = ViewCompat.requireViewById(bottomSheet, R.id.end_time);
        assert endTime.getEditText() != null;
        endTime.getEditText().setOnClickListener(v -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Eindigend voor...")
                    .build();
            picker.addOnPositiveButtonClickListener(selection -> {
                OffsetDateTime offsetDateTime = Instant.ofEpochMilli(selection).atOffset(ZoneOffset.UTC);
                filter.before(offsetDateTime);
            });
            picker.show(getChildFragmentManager(), picker.toString());
        });
        setupBottomSheet();

        associationRecycler.setHasFixedSize(true);
        associationRecycler.setAdapter(associationAdapter);
        ViewCompat.requireViewById(bottomSheet, R.id.select_all).setOnClickListener(
                v -> associationAdapter.setAllChecked(true)
        );
        ViewCompat.requireViewById(bottomSheet, R.id.select_none).setOnClickListener(
                v -> associationAdapter.setAllChecked(false)
        );

        LinearLayout noData = view.findViewById(R.id.events_no_data);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new EmptyViewObserver(recyclerView, noData));

        int secondaryColour = ColourUtils.resolveColour(requireContext(), R.attr.colorSecondary);
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(secondaryColour);

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);
        viewModel.params(filter.getValue());
        viewModel.data().observe(getViewLifecycleOwner(), PartialErrorObserver.with(this::onError));
        viewModel.data().observe(getViewLifecycleOwner(), new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.data().observe(getViewLifecycleOwner(), new SuccessObserver<>() {
            @Override
            protected void onSuccess(@NonNull AssociationRequest.EventItemsAndAssociations data) {
                adapter.setAssociationMap(data.associations());
                adapter.submitData(data.events());
                associationAdapter.itemsAndState(data.associations().requestedAssociations());
            }
        });
        viewModel.refreshing().observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing);
        swipeRefreshLayout.setOnRefreshListener(viewModel);

        view.<Button>findViewById(R.id.events_no_data_button_refresh)
                .setOnClickListener(v -> viewModel.onRefresh());
        view.<Button>findViewById(R.id.events_no_data_button_filters)
                .setOnClickListener(v -> showSheet());
    }

    private void setupBottomSheet() {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheet.setElevation(requireBaseActivity(this).requireToolbar().getElevation());
        bottomSheet.setVisibility(View.VISIBLE);
        bottomToolbar.setNavigationOnClickListener(v -> hideSheet());
        filter.observe(this.getViewLifecycleOwner(), this::filterToInputs);
    }

    private void filterToInputs(EventFilter filter) {
        assert startTime.getEditText() != null;
        if (filter.after() != null) {
            startTime.getEditText().setText(DateUtils.friendlyDateTime(filter.after()));
        } else {
            startTime.getEditText().setText(null);
        }
        assert endTime.getEditText() != null;
        if (filter.before() != null) {
            endTime.getEditText().setText(DateUtils.friendlyDateTime(filter.before()));
        } else {
            endTime.getEditText().setText(null);
        }
        assert searchTerm.getEditText() != null;
        searchTerm.getEditText().setText(filter.term());
    }

    private void doFiltering() {
        // Time stuff is set by the callback.
        this.filter.term(Objects.requireNonNull(searchTerm.getEditText()).getText().toString());
        this.filter.selectedAssociations(associationAdapter.itemsAndState());
        this.viewModel.requestRefresh();
        hideSheet();
    }

    private void onError(Throwable throwable) {
        Log.e(TAG, "Error while getting data.", throwable);
        Snackbar.make(requireView(), getString(R.string.error_network), Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.action_again), v -> viewModel.onRefresh())
                .show();
    }

    @Override
    public void onRemovalScheduled() {
        hideExternalViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideExternalViews();
    }

    private void hideExternalViews() {
        hideSheet();
        bottomSheet.setVisibility(View.GONE);
        bottomSheet.removeAllViews();
        bottomToolbar.setNavigationOnClickListener(null);
    }

    private void hideSheet() {
        bottomSheet.clearFocus();
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void showSheet() {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public boolean onBackPressed() {
        if (behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            Log.d(TAG, "onBackPressed: hiding sheet");
            hideSheet();
            return true;
        }
        return false;
    }
}
