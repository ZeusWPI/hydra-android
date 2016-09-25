package be.ugent.zeus.hydra.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import be.ugent.zeus.hydra.R;

/**
 * Custom dialog to select a time in the preferences.
 *
 * @author Rien Maertens
 *
 * @link http://stackoverflow.com/a/10608622/4424838
 */
public class TimePreference extends DialogPreference {
    private TimePicker picker;
    private Time time;

    public TimePreference(Context context) {
        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        time = new Time();
        setPositiveButtonText(R.string.set);
        setNegativeButtonText(R.string.cancel);

        //We do not want a title.
        setDialogTitle(null);
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
        picker.setCurrentHour(time.getHour());
        picker.setCurrentMinute(time.getMinute());
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            time.set(picker.getCurrentHour(), picker.getCurrentMinute());
            setSummary(getSummary());
            int timeInts = time.toInteger();
            if (callChangeListener(timeInts)) {
                persistInt(timeInts);
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
        if (defaultValue == null) {
            defaultValue = 0;
        }
        if (restoreValue) {
            time.fromObject(getPersistedInt((Integer) defaultValue));
        } else {
            time.fromObject(defaultValue);
        }
        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        return time.toString();
    }
}