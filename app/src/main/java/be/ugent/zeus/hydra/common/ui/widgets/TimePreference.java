package be.ugent.zeus.hydra.common.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.utils.ViewUtils;
import org.threeten.bp.LocalTime;

/**
 * Custom dialog to select a time in the preferences.
 *
 * This dialogs uses {@link LocalTime}, and saves the preference as a time string, such as "10:20". The exact format
 * depends on {@link LocalTime#toString()}, but is ISO-8601.
 *
 * The formatting of the actual picker depends on the locale.
 *
 * @author Rien Maertens
 * @author Niko Strijbol
 * @see <a href="https://github.com/Gericop/Android-Support-Preference-V7-Fix">Based on this library</a>
 * @see LocalTime#toString() The exact documentation on how the value is saved.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class TimePreference extends DialogPreference {

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TimePreference, defStyleAttr, defStyleRes);

        if (ViewUtils.getBoolean(a, R.styleable.TimePreference_useDefaultSummary,
                R.styleable.TimePreference_useDefaultSummary, false)) {
            setSummaryProvider(new TimeSummaryProvider());
        }

        a.recycle();
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        this(context, attrs, ViewUtils.getAttr(context,
                androidx.preference.R.attr.dialogPreferenceStyle,
                android.R.attr.dialogPreferenceStyle));
    }

    public TimePreference(Context context) {
        this(context, null);
    }

    @Nullable
    public LocalTime getTime() {
        String saved = getPersistedString(null);
        if (saved != null) {
            return LocalTime.parse(saved);
        } else {
            return null;
        }
    }

    public void setTime(@NonNull LocalTime time) {
        String oldTime = getPersistedString(null);
        String newTime = time.toString();

        // If the time changed, persist it.
        if (oldTime != null && !oldTime.equals(newTime) || !newTime.equals(oldTime)) {
            persistString(newTime);
            notifyChanged();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(Object defaultValueObj) {
        final String defaultValue = (String) defaultValueObj;
        if (defaultValue != null) {
            LocalTime time = LocalTime.parse(defaultValue);
            setTime(time);
        }
    }

    private static final class TimeSummaryProvider implements SummaryProvider<TimePreference> {
        @Override
        public CharSequence provideSummary(TimePreference preference) {
            LocalTime time = preference.getTime();
            if (time != null) {
                return time.toString();
            } else {
                return "";
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        SavedState myState = new SavedState(superState);
        myState.time = getTime();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setTime(myState.time);
    }

    private static class SavedState extends BaseSavedState {
        private LocalTime time;

        public SavedState(Parcel source) {
            super(source);
            time = (LocalTime) source.readSerializable();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeSerializable(time);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}