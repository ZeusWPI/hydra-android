package be.ugent.zeus.hydra.data.models;

import be.ugent.zeus.hydra.data.gson.ZonedThreeTenUTCAdapter;
import com.google.gson.annotations.JsonAdapter;
import java8.util.Objects;
import org.threeten.bp.ZonedDateTime;

/**
 * @author Niko Strijbol
 */
public final class UrgentInfo {

    private final String name;
    @JsonAdapter(ZonedThreeTenUTCAdapter.class)
    private final ZonedDateTime validUntil;
    private final String url;

    public UrgentInfo(String name, ZonedDateTime validUntil, String url) {
        this.name = name;
        this.validUntil = validUntil;
        this.url = url;
    }

    public ZonedDateTime getValidUntil() {
        return validUntil;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrgentInfo info = (UrgentInfo) o;
        return Objects.equals(name, info.name) &&
                Objects.equals(validUntil, info.validUntil) &&
                Objects.equals(url, info.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, validUntil, url);
    }
}