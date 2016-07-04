package be.ugent.zeus.hydra.models.minerva;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by feliciaan on 29/06/16.
 */
public class WhatsNew implements Serializable{
    @SerializedName("announcement")
    private ArrayList<Announcement> announcements;

    public ArrayList<Announcement> getAnnouncements() {
        return announcements;
    }

    public void setAnnouncements(ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }
}
