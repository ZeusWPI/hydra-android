package be.ugent.zeus.hydra.minerva.dto;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class Agenda {

    private List<AgendaItemDTO> items;

    public List<AgendaItemDTO> getItems() {
        return items;
    }

    public void setItems(List<AgendaItemDTO> items) {
        this.items = items;
    }
}