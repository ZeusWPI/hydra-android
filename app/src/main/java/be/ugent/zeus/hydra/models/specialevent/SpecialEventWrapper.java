package be.ugent.zeus.hydra.models.specialevent;

import com.google.gson.annotations.SerializedName;

/**
 * Created by feliciaan on 06/04/16.
 */
public class SpecialEventWrapper {
    @SerializedName("special-events")
    private SpecialEvents specialEvents;

    public SpecialEvents getSpecialEvents() {
        return specialEvents;
    }

    public void setSpecialEvents(SpecialEvents specialEvents) {
        this.specialEvents = specialEvents;
    }
}
