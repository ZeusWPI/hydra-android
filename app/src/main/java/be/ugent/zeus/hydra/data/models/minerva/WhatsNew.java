package be.ugent.zeus.hydra.data.models.minerva;

import java8.util.Objects;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by feliciaan on 29/06/16.
 */
public final class WhatsNew implements Serializable {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhatsNew whatsNew = (WhatsNew) o;
        return Objects.equals(announcements, whatsNew.announcements) &&
                Objects.equals(agenda, whatsNew.agenda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(announcements, agenda);
    }
}