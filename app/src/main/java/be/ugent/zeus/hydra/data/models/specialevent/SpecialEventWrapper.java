package be.ugent.zeus.hydra.data.models.specialevent;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by feliciaan on 06/04/16.
 */
public class SpecialEventWrapper implements Serializable {

    @SerializedName("special-events")
    private List<SpecialEvent> specialEvents;

    public List<SpecialEvent> getSpecialEvents() {
        return specialEvents;
    }

    public void setSpecialEvents(List<SpecialEvent> specialEvents) {
        this.specialEvents = specialEvents;
    }
}
