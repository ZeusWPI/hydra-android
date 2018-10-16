package be.ugent.zeus.hydra.common.analytics;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java9.util.Objects;


/**
 * An analytics event.
 *
 * @author Niko Strijbol
 */
public class Event {

    private final String event;
    private final Bundle params;

    Event(@NonNull String event) {
        this(event, Bundle.EMPTY);
    }

    Event(@NonNull String event, @NonNull Bundle params) {
        this.event = event;
        this.params = params;
    }

    @NonNull
    public Bundle getParams() {
        return params;
    }

    @NonNull
    public String getEvent() {
        return event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event other = (Event) o;
        return Objects.equals(event, other.event) &&
                Objects.equals(params, other.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, params);
    }
}