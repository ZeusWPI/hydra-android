package be.ugent.zeus.hydra.specialevent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.sko.OverviewActivity;
import com.squareup.moshi.Json;
import java9.util.Objects;
import org.threeten.bp.OffsetDateTime;

/**
 * Model for special events.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
@SuppressWarnings("unused")
public final class SpecialEvent {

    public static final String SKO_IN_APP = "be.ugent.zeus.hydra.special.sko";

    private String name;
    private String link;
    @Json(name = "simple-text")
    private String simpleText;
    private String image;
    private String html;
    private int priority;
    private OffsetDateTime start;
    private OffsetDateTime end;
    private boolean development;
    @Json(name = "in-app")
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
        if (SKO_IN_APP.equals(inApp)) {
            return OverviewActivity.start(context);
        }
        return new Intent(Intent.ACTION_VIEW, Uri.parse(getLink()));
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

    public OffsetDateTime getStart() {
        return start;
    }

    public OffsetDateTime getEnd() {
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