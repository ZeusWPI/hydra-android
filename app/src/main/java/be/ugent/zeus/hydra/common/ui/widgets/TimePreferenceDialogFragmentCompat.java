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

package be.ugent.zeus.hydra.common.ui.widgets;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceDialogFragmentCompat;

import java.time.LocalTime;

/**
 * Custom dialog to select a time in the preferences. This works together with {@link TimePreference}.
 * <p>
 * This dialogs uses {@link LocalTime} as the object.
 *
 * @author Niko Strijbol
 * @see <a href="https://github.com/Gericop/Android-Support-Preference-V7-Fix">Based on this library</a>
 * @see LocalTime#toString() The exact documentation on how the value is saved.
 */
public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat implements TimePickerDialog.OnTimeSetListener {

    protected LocalTime time;

    public static TimePreferenceDialogFragmentCompat newInstance(String key) {
        final TimePreferenceDialogFragmentCompat fragment = new TimePreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TimePreference preference = (TimePreference) getPreference();

        LocalTime time = preference.getTime();
        if (time == null) {
            time = LocalTime.now();
        }

        int hour = time.getHour();
        int minute = time.getMinute();
        boolean is24hours = DateFormat.is24HourFormat(requireContext());

        TimePickerDialog dialog = new TimePickerDialog(requireContext(), this, hour, minute, is24hours);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, preference.getPositiveButtonText(), this);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, preference.getNegativeButtonText(), this);

        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        if (which == DialogInterface.BUTTON_POSITIVE) {
            TimePickerDialog pickerDialog = (TimePickerDialog) dialog;
            pickerDialog.onClick(dialog, which);
        }
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        TimePreference preference = (TimePreference) getPreference();

        if (positiveResult && preference.callChangeListener(time)) {
            preference.setTime(time);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.time = LocalTime.of(hourOfDay, minute);
        super.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
    }
}