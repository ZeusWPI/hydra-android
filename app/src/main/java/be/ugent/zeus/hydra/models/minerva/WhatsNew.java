package be.ugent.zeus.hydra.models.minerva;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by feliciaan on 29/06/16.
 */
public class WhatsNew implements Serializable {

    @SerializedName("announcement")
    private ArrayList<Announcement> announcements;

    @SerializedName("agenda")
    private ArrayList<AgendaItem> agenda;

    public List<Announcement> getAnnouncements() {
        if(announcements == null) {
            announcements = new ArrayList<>();
        }
        return announcements;
    }

    public void setAnnouncements(ArrayList<Announcement> announcements) {
        this.announcements = announcements;
    }

    public List<AgendaItem> getAgenda() {
        if(agenda == null) {
            agenda = new ArrayList<>();
        }
        return agenda;
    }

    public void setAgenda(ArrayList<AgendaItem> agenda) {
        this.agenda = agenda;
    }
}