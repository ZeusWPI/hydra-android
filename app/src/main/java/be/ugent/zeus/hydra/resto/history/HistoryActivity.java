package be.ugent.zeus.hydra.resto.history;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.arch.observers.ErrorObserver;
import be.ugent.zeus.hydra.common.arch.observers.ProgressObserver;
import be.ugent.zeus.hydra.common.arch.observers.SuccessObserver;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.SingleDayFragment;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.Month;
import org.threeten.bp.ZoneId;

public class HistoryActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    private static final String ARG_DATE = "arg_date";

    private static final String TAG = "HistoryActivity";

    private LocalDate localDate;
    private SingleDayViewModel viewModel;

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

        TextView errorView = findViewById(R.id.error_view);
        viewModel = ViewModelProviders.of(this).get(SingleDayViewModel.class);
        viewModel.setInitialDate(localDate);
        viewModel.getData().observe(this, new SuccessObserver<RestoMenu>() {
            @Override
            protected void onSuccess(RestoMenu data) {
                Log.d(TAG, "onSuccess: GOT DATA");
                // Add the fragment
                errorView.setVisibility(View.GONE);
                setTitle(DateUtils.getFriendlyDate(data.getDate()));
                showFragment(data);
            }
        });
        viewModel.getData().observe(this, new ErrorObserver<RestoMenu>() {
            @Override
            protected void onError(RequestException throwable) {
                Log.d(TAG, "Error", throwable);
                hideFragment();
                setTitle(R.string.resto_history_error);
                errorView.setVisibility(View.VISIBLE);
                errorView.setText(getString(R.string.resto_history_not_found, DateUtils.getFriendlyDate(localDate)));
            }
        });
        viewModel.getData().observe(this, new ProgressObserver<>(findViewById(R.id.progress_bar)));

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

    private DatePickerDialog createAndSetupDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        DatePicker picker = datePickerDialog.getDatePicker();
        // Hard coded earliest date
        LocalDate earliest = LocalDate.of(2016, Month.FEBRUARY, 1);
        LocalDate furthest = LocalDate.now().plusWeeks(2).plusDays(1);
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
}