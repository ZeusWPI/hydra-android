/*
 * Copyright (c) 2023 The Hydra authors
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
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.databinding.ActivityRestoHistoryBinding;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.SingleDayFragment;
import be.ugent.zeus.hydra.resto.meta.selectable.SelectableMetaViewModel;
import be.ugent.zeus.hydra.resto.meta.selectable.SelectedResto;

public class HistoryActivity
        extends BaseActivity<ActivityRestoHistoryBinding>
        implements DatePickerDialog.OnDateSetListener {

    private static final String ARG_DATE = "arg_date";

    private static final String TAG = "HistoryActivity";

    private LocalDate localDate;
    private RestoChoice restoChoice;
    private SingleDayViewModel viewModel;

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

        binding.exposedDropdown.setEnabled(false);
        binding.exposedDropdownContents.setText(getString(R.string.resto_spinner_loading), false);

        final ViewModelProvider provider = new ViewModelProvider(this);

        viewModel = provider.get(SingleDayViewModel.class);
        viewModel.changeDate(localDate); // Set the initial date
        viewModel.data().observe(this, new SuccessObserver<>() {
            @Override
            protected void onSuccess(@NonNull RestoMenu data) {
                // Add the fragment
                binding.errorView.setVisibility(View.GONE);
                setTitle(getString(R.string.resto_history_title, DateUtils.friendlyDate(HistoryActivity.this, data.date())));
                showFragment(data);
            }
        });
        viewModel.data().observe(this, PartialErrorObserver.with(this::onError));
        viewModel.data().observe(this, new ProgressObserver<>(binding.progressBar));

        SelectableMetaViewModel metaViewModel = provider.get(SelectableMetaViewModel.class);
        metaViewModel.data().observe(this, SuccessObserver.with(this::onReceiveRestoChoices));

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
            binding.errorView.setText(getString(R.string.resto_history_not_found, DateUtils.friendlyDate(this, localDate)));
        }
    }

    private void onReceiveRestoChoices(List<RestoChoice> choices) {
        var selectedRestoIndex = SelectedResto.findChoiceIndex(this, choices, restoChoice);
        var choice = choices.get(selectedRestoIndex);
        viewModel.changeResto(choice);
        ArrayAdapter<RestoChoice> items = new ArrayAdapter<>(this, R.layout.x_simple_spinner_dropdown_item);
        items.addAll(choices);
        binding.exposedDropdownContents.setAdapter(items);
        binding.exposedDropdownContents.setText(choice.name(), false);
        binding.exposedDropdownContents.setSelection(selectedRestoIndex);
        binding.exposedDropdownContents.setEnabled(true);
        binding.exposedDropdownContents.setOnItemClickListener(this::onRestoSelected);
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

    public void onRestoSelected(AdapterView<?> parent, View view, int position, long id) {
        var selectedChoice = (RestoChoice) parent.getItemAtPosition(position);
        binding.exposedDropdown.clearFocus();
        if (selectedChoice == null || selectedChoice.endpoint() == null) {
            // Do nothing, as this should not happen.
            return;
        }
        
        restoChoice = selectedChoice;
        viewModel.changeResto(restoChoice);
    }
}
