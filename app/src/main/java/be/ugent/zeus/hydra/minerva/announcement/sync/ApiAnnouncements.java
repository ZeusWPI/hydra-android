package be.ugent.zeus.hydra.minerva.announcement.sync;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * Only the announcements are extracted. Other things are ignored.
 *
 * @author Niko Strijbol
 */
final class ApiAnnouncements {
    @Json(name = "items")
    public List<ApiAnnouncement> announcements;
}