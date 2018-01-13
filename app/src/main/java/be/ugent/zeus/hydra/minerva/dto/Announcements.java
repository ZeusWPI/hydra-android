package be.ugent.zeus.hydra.minerva.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Only the announcements are extracted. Other things are ignored.
 *
 * @author Niko Strijbol
 */
public class Announcements {

    @SerializedName("items")
    private List<AnnouncementDTO> announcements;

    public List<AnnouncementDTO> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<AnnouncementDTO> announcements) {
        this.announcements = announcements;
    }
}