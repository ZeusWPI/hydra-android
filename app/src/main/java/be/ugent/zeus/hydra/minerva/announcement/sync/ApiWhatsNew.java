package be.ugent.zeus.hydra.minerva.announcement.sync;

import com.squareup.moshi.Json;
import java8.util.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * The result of the "what's new" request of the API. This request contains additional data not present in this class,
 * since we don't need it.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
final class ApiWhatsNew {

    @Json(name = "announcement")
    public List<ApiAnnouncement> announcements = new ArrayList<>();

    public ApiWhatsNew() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiWhatsNew that = (ApiWhatsNew) o;
        return Objects.equals(announcements, that.announcements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(announcements);
    }
}