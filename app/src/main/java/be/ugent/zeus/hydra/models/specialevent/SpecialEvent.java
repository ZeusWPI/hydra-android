package be.ugent.zeus.hydra.models.specialevent;

import android.content.Intent;
import android.net.Uri;
import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * Model for special events.
 *
 * @author Niko Strijbol
 * @author feliciaan
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

    private Intent viewIntent;

    public void setViewIntent(Intent intent) {
        this.viewIntent = intent;
    }

    public Intent getViewIntent() {
        if(viewIntent == null) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(getLink()));
        } else {
            return viewIntent;
        }
    }

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
}