package be.ugent.zeus.hydra.data.models.minerva;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Only the announcements are extracted. Other things are ignored.
 *
 * @author Niko Strijbol
 */
public class Announcements {

    @SerializedName("items")
    private List<Announcement> announcements;

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }
}