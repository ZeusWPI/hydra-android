package be.ugent.zeus.hydra.common.reporting;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

/**
 * An analytics event. This event is implementation-independent; at the same time the values are defined by the
 * implementation itself.
 * <p>
 * A selection of predefined types is available in {@link BaseEvents}. Get an implementation from the {@link
 * Reporting}.
 *
 * @author Niko Strijbol
 */
public interface Event {

    /**
     * @return Parameters to be included in the log for this event. The implementation might impose restrictions, such
     * as an enumeration. If {@link #getEventName()} returns {@code null}, the return value of this method is
     * undefined.
     */
    @Nullable
    default Bundle getParams() {
        return null;
    }

    /**
     * @return The name of the event for the log. The implementation might impose restrictions, such as predefined
     * names, name length or name uniqueness. If {@code null}, the event will not be logged.
     */
    @Nullable
    String getEventName();

    /**
     * @return The name of the event for the log. The implementation might impose restrictions, such as predefined
     * names, name length or name uniqueness. If not present, it will not be logged.
     */
    @NonNull
    default Optional<String> getEvent() {
        return Optional.ofNullable(getEventName());
    }
}
