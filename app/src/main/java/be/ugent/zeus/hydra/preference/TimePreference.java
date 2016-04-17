package be.ugent.zeus.hydra.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;

import be.ugent.zeus.hydra.R;

/**
 * @author Rien Maertens
 * http://stackoverflow.com/a/10608622/4424838
 */
public class TimePreference extends DialogPreference{
    private TimePicker picker;
    private int hour = 11;
    private int minute = 0;

    public TimePreference(Context context){
        this(context, null);
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        setPositiveButtonText(R.string.set);
        setNegativeButtonText(R.string.cancel);
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(DateFormat.is24HourFormat(getContext()));
        return picker;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setCurrentHour(hour);
        picker.setCurrentMinute(minute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            hour = picker.getCurrentHour();
            minute = picker.getCurrentMinute();
            String summary = getSummary().toString();
            setSummary(summary);
            if (callChangeListener(summary)) {
                persistString(summary);
                notifyChanged();
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String value;
        if (restoreValue) {
            value = getPersistedString((String)defaultValue);
        } else {
            value = ((String)defaultValue);
        }
        String values[] = value.split(":");
        hour = Integer.parseInt(values[0], 10);
        minute = Integer.parseInt(values[1], 10);
        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        return String.format(Locale.ENGLISH, "%02d:%02d", hour, minute);
    }

}
