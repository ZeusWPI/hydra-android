package be.ugent.zeus.hydra.resto.history;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.network.IOFailureException;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.NoPaddingArrayAdapter;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.SingleDayFragment;
import be.ugent.zeus.hydra.resto.meta.selectable.SelectableMetaViewModel;
import be.ugent.zeus.hydra.resto.meta.selectable.SelectedResto;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.ZoneId;

import java.util.List;

public class HistoryActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private static final String ARG_DATE = "arg_date";

    private static final String TAG = "HistoryActivity";

    private LocalDate localDate;
    private RestoChoice restoChoice;
    private SingleDayViewModel viewModel;
    private Spinner restoSpinner;
    private ProgressBar restoProgressBar;
    private ArrayAdapter<SelectedResto.Wrapper> restoAdapter;
    private TextView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resto_history);

        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_DATE)) {
            localDate = (LocalDate) savedInstanceState.getSerializable(ARG_DATE);
        } else if (getIntent().hasExtra(ARG_DATE)) {
            localDate = (LocalDate) getIntent().getSerializableExtra(ARG_DATE);
        } else {
            localDate = LocalDate.now();
        }

        Toolbar bottomToolbar = findViewById(R.id.bottom_toolbar);

        restoSpinner = findViewById(R.id.resto_spinner);
        restoSpinner.setEnabled(false);
        restoProgressBar = findViewById(R.id.resto_progress_bar);
        restoAdapter = new NoPaddingArrayAdapter<>(bottomToolbar.getContext(), android.R.layout.simple_spinner_item);
        restoAdapter.add(new SelectedResto.Wrapper(getString(R.string.resto_spinner_loading)));
        restoAdapter.setDropDownViewResource(R.layout.x_simple_spinner_dropdown_item);
        restoSpinner.setAdapter(restoAdapter);

        errorView = findViewById(R.id.error_view);
        viewModel = ViewModelProviders.of(this).get(SingleDayViewModel.class);
        viewModel.changeDate(localDate); // Set the initial date
        viewModel.getData().observe(this, new SuccessObserver<RestoMenu>() {
            @Override
            protected void onSuccess(RestoMenu data) {
                // Add the fragment
                errorView.setVisibility(View.GONE);
                setTitle(getString(R.string.resto_history_title, DateUtils.getFriendlyDate(data.getDate())));
                showFragment(data);
            }
        });
        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(findViewById(R.id.progress_bar)));

        SelectableMetaViewModel metaViewModel = ViewModelProviders.of(this).get(SelectableMetaViewModel.class);
        metaViewModel.getData().observe(this, SuccessObserver.with(this::onReceiveRestos));

        findViewById(R.id.fab).setOnClickListener(v -> createAndSetupDialog().show());
    }

    private void showFragment(RestoMenu data) {
        SingleDayFragment fragment = SingleDayFragment.newInstance(data);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void hideFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            manager.beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    private void onError(Throwable throwable) {
        Log.w(TAG, "Error", throwable);
        hideFragment();
        setTitle(R.string.resto_history_error);
        errorView.setVisibility(View.VISIBLE);
        if (throwable instanceof IOFailureException) {
            errorView.setText(R.string.no_network);
        } else {
            errorView.setText(getString(R.string.resto_history_not_found, DateUtils.getFriendlyDate(localDate)));
        }
    }

    private void onReceiveRestos(List<RestoChoice> choices) {
        SelectedResto selectedResto = new SelectedResto(this);
        selectedResto.setSelected(restoChoice);
        selectedResto.setData(choices);
        viewModel.changeResto(selectedResto.getSelected());

        List<SelectedResto.Wrapper> wrappers = selectedResto.getAsWrappers();
        restoAdapter.clear();
        restoAdapter.addAll(wrappers);
        restoSpinner.setSelection(selectedResto.getSelectedIndex(), false);
        restoSpinner.setEnabled(true);
        restoProgressBar.setVisibility(View.GONE);
        restoSpinner.setOnItemSelectedListener(this);
    }

    private DatePickerDialog createAndSetupDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        DatePicker picker = datePickerDialog.getDatePicker();
        // Hard coded earliest date
        LocalDate earliest = LocalDate.of(2016, Month.FEBRUARY, 1);
        LocalDate furthest = LocalDate.now();
        ZoneId defaultZone = ZoneId.systemDefault();
        picker.setMinDate(earliest.atStartOfDay(defaultZone).toInstant().toEpochMilli());
        picker.setMaxDate(furthest.atStartOfDay(defaultZone).toInstant().toEpochMilli());
        return datePickerDialog;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_DATE, localDate);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        localDate = LocalDate.of(year, month + 1, dayOfMonth);
        viewModel.changeDate(localDate);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SelectedResto.Wrapper wrapper = (SelectedResto.Wrapper) parent.getItemAtPosition(position);
        restoChoice = wrapper.resto;

        if (restoChoice == null || restoChoice.getEndpoint() == null) {
            // Do nothing, as this should not happen.
            return;
        }
        viewModel.changeResto(restoChoice);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }
}