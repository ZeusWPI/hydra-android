package be.ugent.zeus.hydra.urgent;

import be.ugent.zeus.hydra.common.converter.LocalZonedDateTime;
import java9.util.Objects;
import org.threeten.bp.ZonedDateTime;

/**
 * @author Niko Strijbol
 */
public final class UrgentInfo {

    @LocalZonedDateTime
    private final ZonedDateTime validUntil;
    private final String url;
    private final ProgrammeInformation meta;

    public UrgentInfo(String name, ZonedDateTime validUntil, String url, ProgrammeInformation meta) {
        this.validUntil = validUntil;
        this.url = url;
        this.meta = meta;
    }

    public ZonedDateTime getValidUntil() {
        return validUntil;
    }


    public String getUrl() {
        return url;
    }

    public ProgrammeInformation getMeta() {
        return meta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrgentInfo info = (UrgentInfo) o;
        return Objects.equals(validUntil, info.validUntil) &&
                Objects.equals(url, info.url) &&
                Objects.equals(meta, info.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validUntil, url, meta);
    }
}