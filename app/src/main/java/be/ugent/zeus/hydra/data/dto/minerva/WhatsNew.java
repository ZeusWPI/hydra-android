package be.ugent.zeus.hydra.data.dto.minerva;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by feliciaan on 29/06/16.
 */
public final class WhatsNew {

    @SerializedName("announcement")
    private ArrayList<AnnouncementDTO> announcements;

    @SerializedName("agenda")
    private ArrayList<AgendaItemDTO> agenda;

    public List<AnnouncementDTO> getAnnouncements() {
        if(announcements == null) {
            announcements = new ArrayList<>();
        }
        return announcements;
    }

    public void setAnnouncements(ArrayList<AnnouncementDTO> announcements) {
        this.announcements = announcements;
    }

    public List<AgendaItemDTO> getAgenda() {
        if(agenda == null) {
            agenda = new ArrayList<>();
        }
        return agenda;
    }

    public void setAgenda(ArrayList<AgendaItemDTO> agenda) {
        this.agenda = agenda;
    }
}