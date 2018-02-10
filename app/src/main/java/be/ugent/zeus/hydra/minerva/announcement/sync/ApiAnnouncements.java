package be.ugent.zeus.hydra.minerva.announcement.sync;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Only the announcements are extracted. Other things are ignored.
 *
 * @author Niko Strijbol
 */
final class ApiAnnouncements {
    @SerializedName("items")
    public List<ApiAnnouncement> announcements;
}