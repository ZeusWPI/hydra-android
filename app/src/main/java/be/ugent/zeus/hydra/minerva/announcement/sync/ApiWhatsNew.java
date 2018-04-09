package be.ugent.zeus.hydra.minerva.announcement.sync;

import com.squareup.moshi.Json;

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
}