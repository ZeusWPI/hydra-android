package be.ugent.zeus.hydra.models.minerva;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by feliciaan on 29/06/16.
 */
public class WhatsNew implements Serializable {

    @SerializedName("announcement")
    private List<Announcement> announcements;

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(List<Announcement> announcements) {
        this.announcements = announcements;
    }
}