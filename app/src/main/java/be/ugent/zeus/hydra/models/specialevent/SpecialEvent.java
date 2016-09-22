package be.ugent.zeus.hydra.models.specialevent;

import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * Created by feliciaan on 06/04/16.
 */
@SuppressWarnings("unused")
public class SpecialEvent implements Serializable {

    private String name;
    private String link;
    @SerializedName("simple-text")
    private String simpleText;
    private String image;
    private String html;
    private int priority;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime start;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime end;
    private boolean development;

    private boolean sko = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHtml() {
        return html;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    public boolean isDevelopment() {
        return development;
    }

    public boolean isSko() {
        return sko;
    }

    public void setSko(boolean sko) {
        this.sko = sko;
    }
}