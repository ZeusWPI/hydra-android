package be.ugent.zeus.hydra.ui.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import be.ugent.zeus.hydra.R;
import org.threeten.bp.LocalTime;

/**
 * Custom dialog to select a time in the preferences.
 *
 * This dialogs uses {@link LocalTime}, and saves the preference as a time string, such as "10:20".
 *
 * @author Rien Maertens
 * @author Niko Strijbol
 *
 * @see <a href="http://stackoverflow.com/a/10608622/4424838">Based on this</a>
 * @see LocalTime#toString() The exact documentation on how the value is saved.
 */
public class TimePreference extends DialogPreference {

    private TimePicker picker;
    protected LocalTime time;

    public TimePreference(Context context) {
        this(context, null);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.dialogPreferenceStyle);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        time = LocalTime.now();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setHour(time.getHour());
        } else {
            //noinspection deprecation
            picker.setCurrentHour(time.getHour());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            picker.setMinute(time.getMinute());
        } else {
            //noinspection deprecation
            picker.setCurrentMinute(time.getMinute());
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                time = LocalTime.of(picker.getHour(), picker.getMinute());
            } else {
                //noinspection deprecation
                time = LocalTime.of(picker.getCurrentHour(), picker.getCurrentMinute());
            }

            setSummary(getSummary());

            if (callChangeListener(time)) {
                persistString(time.toString());
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
        Object defaultValue1 = defaultValue;
        if (defaultValue1 == null) {
            defaultValue1 = "00:00";
        }
        if (restoreValue) {
            time = LocalTime.parse(getPersistedString((String) defaultValue1));
        } else {
            time = LocalTime.parse((CharSequence) defaultValue1);
        }
        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        return time.toString();
    }
}