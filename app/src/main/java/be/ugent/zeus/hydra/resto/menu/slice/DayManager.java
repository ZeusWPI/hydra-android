package be.ugent.zeus.hydra.resto.menu.slice;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.threeten.bp.LocalDate;

/**
 * Manages which day should be displayed on the slice.
 *
 * TODO: this is done as in the samples, but is this the best way?
 *
 * @author Niko Strijbol
 */
final class DayManager {

    private static final Object sync = new Object();
    private static LocalDate date;

    private DayManager() {
        // No
    }

    /**
     * @return The date to show in the slice.
     */
    @NonNull
    public static LocalDate getDate() {
        synchronized (sync) {
            if (date == null) {
                date = LocalDate.now();
            }
        }
        return date;
    }

    /**
     * Set a new date. Note that the caller is responsible for notifying the slice of the update.
     *
     * @param newDate The new date.
     */
    public static void setDate(@Nullable LocalDate newDate) {
        synchronized (sync) {
            date = newDate;
        }
    }
}