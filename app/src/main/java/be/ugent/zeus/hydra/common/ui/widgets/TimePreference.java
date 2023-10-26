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

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.os.ParcelCompat;
import androidx.preference.DialogPreference;

import java.time.LocalTime;

/**
 * Custom dialog to select a time in the preferences.
 * <p>
 * This dialogs uses {@link LocalTime}, and saves the preference as a time string, such as "10:20". The exact format
 * depends on {@link LocalTime#toString()}, but is ISO-8601.
 * <p>
 * The formatting of the actual picker depends on the locale.
 *
 * @author Rien Maertens
 * @author Niko Strijbol
 * @noinspection unused
 * @see <a href="https://github.com/Gericop/Android-Support-Preference-V7-Fix">Based on this library</a>
 * @see LocalTime#toString() The exact documentation on how the value is saved.
 */
public class TimePreference extends DialogPreference {

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setSummaryProvider(new TimeSummaryProvider());
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSummaryProvider(new TimeSummaryProvider());
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSummaryProvider(new TimeSummaryProvider());
    }

    public TimePreference(Context context) {
        super(context);
        setSummaryProvider(new TimeSummaryProvider());
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

    private static class SavedState extends BaseSavedState {

        private LocalTime time;

        public SavedState(Parcel source) {
            super(source);
            time = (LocalTime) ParcelCompat.readSerializable(source, LocalTime.class.getClassLoader(), LocalTime.class);
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
                new Parcelable.Creator<>() {
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