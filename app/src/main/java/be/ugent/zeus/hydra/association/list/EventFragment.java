package be.ugent.zeus.hydra.association.list;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.*;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import java9.lang.Iterables;
import java9.util.Comparators;
import java9.util.Iterators;
import java9.util.Spliterator;
import java9.util.Spliterators;
import java9.util.function.Function;
import java9.util.function.Predicate;
import java9.util.stream.Collectors;
import java9.util.stream.StreamSupport;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.arch.observers.AdapterObserver;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.recyclerview.EmptyViewObserver;
import be.ugent.zeus.hydra.common.utils.ColourUtils;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.preferences.PreferenceActivity;
import be.ugent.zeus.hydra.preferences.PreferenceEntry;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;

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
    private EventViewModel viewModel;

    private FrameLayout bottomSheet;
    private BottomSheetBehavior<FrameLayout> behavior;
    private Toolbar bottomToolbar;

    private final Filter.Live filter = new Filter.Live();
    private TextInputLayout searchTerm;
    private TextInputLayout startTime;
    private TextInputLayout endTime;
    private final AssociationsAdapter associationAdapter = new AssociationsAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activities, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                filter.setAfter(offsetDateTime);
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
                filter.setBefore(offsetDateTime);
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
        viewModel.setParams(filter.getValue());
        viewModel.getData().observe(getViewLifecycleOwner(), PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(getViewLifecycleOwner(), new ProgressObserver<>(view.findViewById(R.id.progress_bar)));
        viewModel.getData().observe(getViewLifecycleOwner(), new SuccessObserver<Pair<List<EventItem>, List<Association>>>() {
            @Override
            protected void onSuccess(@NonNull Pair<List<EventItem>, List<Association>> data) {
                adapter.submitData(data.first);
                List<Pair<Association, Boolean>> mapped = StreamSupport.stream(data.second)
                        .map(association -> new Pair<>(association, filter.filter.getAssociations().contains(association.getAbbreviation())))
                        .sorted(Comparators.comparing(associationBooleanPair -> associationBooleanPair.first.getName()))
                        .collect(Collectors.toList());
                associationAdapter.setItemsAndState(mapped);
            }
        });
        viewModel.getRefreshing().observe(getViewLifecycleOwner(), swipeRefreshLayout::setRefreshing);
        swipeRefreshLayout.setOnRefreshListener(viewModel);

        Button refresh = view.findViewById(R.id.events_no_data_button_refresh);
        Button filters = view.findViewById(R.id.events_no_data_button_filters);

        refresh.setOnClickListener(v -> viewModel.onRefresh());
        filters.setOnClickListener(v -> PreferenceActivity.start(requireContext(), PreferenceEntry.ACTIVITIES));
    }

    private void setupBottomSheet() {
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheet.setElevation(requireBaseActivity(this).requireToolbar().getElevation());
        bottomSheet.setVisibility(View.VISIBLE);
        bottomToolbar.setNavigationOnClickListener(v -> hideSheet());
        filter.observe(this.getViewLifecycleOwner(), this::filterToInputs);
    }

    private void filterToInputs(Filter filter) {
        assert startTime.getEditText() != null;
        if (filter.getAfter() != null) {
            startTime.getEditText().setText(DateUtils.getFriendlyDateTime(filter.getAfter()));
        } else {
            startTime.getEditText().setText(null);
        }
        assert endTime.getEditText() != null;
        if (filter.getBefore() != null) {
            endTime.getEditText().setText(DateUtils.getFriendlyDateTime(filter.getBefore()));
        } else {
            endTime.getEditText().setText(null);
        }
        assert searchTerm.getEditText() != null;
        searchTerm.getEditText().setText(filter.getTerm());
    }
    
    private void doFiltering() {
        // Time stuff is set by the callback.
        this.filter.setTerm(searchTerm.getEditText().getText().toString());
        Set<String> disabled = StreamSupport.stream(Iterables.spliterator(associationAdapter.getItemsAndState()), false)
                .filter(associationBooleanPair -> associationBooleanPair.second)
                .map(associationBooleanPair -> associationBooleanPair.first.getAbbreviation())
                .collect(Collectors.toSet());
        this.filter.setAssociations(disabled);
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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_events, menu);
        requireBaseActivity(this).tintToolbarIcons(menu, R.id.action_refresh, R.id.action_search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                viewModel.onRefresh();
                return true;
            case R.id.action_search:
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
