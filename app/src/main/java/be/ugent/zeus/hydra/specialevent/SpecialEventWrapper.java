package be.ugent.zeus.hydra.specialevent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import com.squareup.moshi.Json;
import java9.util.Objects;

/**
 * Created by feliciaan on 06/04/16.
 */
public final class SpecialEventWrapper {

    @Json(name = "special-events")
    private List<SpecialEvent> specialEvents;

    @NonNull
    public List<SpecialEvent> getSpecialEvents() {
        if (specialEvents == null) {
            specialEvents = new ArrayList<>();
        }
        return specialEvents;
    }

    public void setSpecialEvents(List<SpecialEvent> specialEvents) {
        this.specialEvents = specialEvents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecialEventWrapper that = (SpecialEventWrapper) o;
        return Objects.equals(specialEvents, that.specialEvents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(specialEvents);
    }
}
