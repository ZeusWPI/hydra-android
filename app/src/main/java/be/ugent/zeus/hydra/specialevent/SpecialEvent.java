/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.specialevent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;

import java.time.OffsetDateTime;
import java.util.Objects;

import be.ugent.zeus.hydra.sko.OverviewActivity;
import com.squareup.moshi.Json;

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

    @NonNull
    public OffsetDateTime getStart() {
        return start;
    }

    public OffsetDateTime getEnd() {
        return end;
    }

    public boolean isDevelopment() {
        return development;
    }

    public void setDevelopment(boolean development) {
        this.development = development;
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
}