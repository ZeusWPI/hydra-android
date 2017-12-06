package be.ugent.zeus.hydra.data.models.minerva;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class Agenda {

    private List<AgendaItem> items;

    public List<AgendaItem> getItems() {
        return items;
    }

    public void setItems(List<AgendaItem> items) {
        this.items = items;
    }
}
