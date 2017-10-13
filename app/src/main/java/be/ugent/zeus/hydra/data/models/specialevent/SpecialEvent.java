package be.ugent.zeus.hydra.data.models.specialevent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.gson.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.ui.sko.overview.OverviewActivity;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * Model for special events.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
@SuppressWarnings("unused")
public final class SpecialEvent implements Serializable {

    public static final String SKO_IN_APP = "be.ugent.zeus.hydra.special.sko";

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
    @SerializedName("in-app")
    private String inApp;

    private long id;

    /**
     * Get the intent to view this event. This places the responsibility on the event. By default, the browser is
     * opened, but a custom intent can be set.
     *
     * @return The intent.
     */
    @NonNull
    public Intent getViewIntent(Context context) {
        // Prevent nullpointer in switch.
        if (inApp == null) {
            inApp = "";
        }
        switch (inApp) {
            case SKO_IN_APP:
                return new Intent(context, OverviewActivity.class);
            default:
                return new Intent(Intent.ACTION_VIEW, Uri.parse(getLink()));
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
                id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(link, that.link) &&
                Objects.equals(simpleText, that.simpleText) &&
                Objects.equals(image, that.image) &&
                Objects.equals(html, that.html) &&
                Objects.equals(start, that.start) &&
                Objects.equals(inApp, that.inApp) &&
                Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, link, simpleText, image, html, priority, start, end, inApp);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setInApp(String inApp) {
        this.inApp = inApp;
    }

    public void setDevelopment(boolean development) {
        this.development = development;
    }
}