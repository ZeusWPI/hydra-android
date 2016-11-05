package be.ugent.zeus.hydra.models.specialevent;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.Objects;
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

    /**
     * Set the viewing intent. Passing null will cause the default intent to be used.
     * @param intent The intent or null for the default.
     */
    public void setViewIntent(@Nullable Intent intent) {
        this.viewIntent = intent;
    }

    /**
     * Get the intent to view this event. This places the responsibility on the event. By default, the browser is
     * opened, but a custom intent can be set.
     *
     * @return The intent.
     */
    @NonNull
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpecialEvent that = (SpecialEvent) o;
        return priority == that.priority &&
                Objects.equals(name, that.name) &&
                Objects.equals(link, that.link) &&
                Objects.equals(simpleText, that.simpleText) &&
                Objects.equals(image, that.image) &&
                Objects.equals(html, that.html) &&
                Objects.equals(start, that.start) &&
                Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, link, simpleText, image, html, priority, start, end);
    }
}