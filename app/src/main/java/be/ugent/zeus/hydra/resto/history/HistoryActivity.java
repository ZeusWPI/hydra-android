package be.ugent.zeus.hydra.resto.history;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.PartialErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.network.IOFailureException;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.NoPaddingArrayAdapter;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.databinding.ActivityRestoHistoryBinding;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.SingleDayFragment;
import be.ugent.zeus.hydra.resto.meta.selectable.SelectableMetaViewModel;
import be.ugent.zeus.hydra.resto.meta.selectable.SelectedResto;

public class HistoryActivity
        extends BaseActivity<ActivityRestoHistoryBinding>
        implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    private static final String ARG_DATE = "arg_date";

    private static final String TAG = "HistoryActivity";

    private LocalDate localDate;
    private RestoChoice restoChoice;
    private SingleDayViewModel viewModel;
    private ArrayAdapter<SelectedResto.Wrapper> restoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityRestoHistoryBinding::inflate);

        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_DATE)) {
            localDate = (LocalDate) savedInstanceState.getSerializable(ARG_DATE);
        } else if (getIntent().hasExtra(ARG_DATE)) {
            localDate = (LocalDate) getIntent().getSerializableExtra(ARG_DATE);
        } else {
            localDate = LocalDate.now();
        }

        binding.restoSpinner.setEnabled(false);
        restoAdapter = new NoPaddingArrayAdapter<>(binding.bottomToolbar.getContext(), R.layout.x_spinner_title_resto);
        restoAdapter.add(new SelectedResto.Wrapper(getString(R.string.resto_spinner_loading)));
        restoAdapter.setDropDownViewResource(R.layout.x_simple_spinner_dropdown_item);
        binding.restoSpinner.setAdapter(restoAdapter);

        final ViewModelProvider provider = new ViewModelProvider(this);

        viewModel = provider.get(SingleDayViewModel.class);
        viewModel.changeDate(localDate); // Set the initial date
        viewModel.getData().observe(this, new SuccessObserver<RestoMenu>() {
            @Override
            protected void onSuccess(@NonNull RestoMenu data) {
                // Add the fragment
                binding.errorView.setVisibility(View.GONE);
                setTitle(getString(R.string.resto_history_title, DateUtils.getFriendlyDate(HistoryActivity.this, data.getDate())));
                showFragment(data);
            }
        });
        viewModel.getData().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.getData().observe(this, new ProgressObserver<>(binding.progressBar));

        SelectableMetaViewModel metaViewModel = provider.get(SelectableMetaViewModel.class);
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
        binding.errorView.setVisibility(View.VISIBLE);
        if (throwable instanceof IOFailureException) {
            binding.errorView.setText(R.string.error_network);
        } else {
            binding.errorView.setText(getString(R.string.resto_history_not_found, DateUtils.getFriendlyDate(this, localDate)));
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
        binding.restoSpinner.setSelection(selectedResto.getSelectedIndex(), false);
        binding.restoSpinner.setEnabled(true);
        binding.restoProgressBar.setVisibility(View.GONE);
        binding.restoSpinner.setOnItemSelectedListener(this);
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
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
