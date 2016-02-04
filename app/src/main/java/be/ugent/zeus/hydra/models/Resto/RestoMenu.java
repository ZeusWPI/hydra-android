package be.ugent.zeus.hydra.models.Resto;

import java.util.Date;

/**
 * Created by feliciaan on 15/10/15.
 */
public class RestoMenu {
    private boolean open;
    private Date date;
    private RestoItem[] meat;
    private String[] vegetables;

    public RestoMenu() {};

    public RestoMenu(boolean open, Date date, RestoItem[] meat, String[] vegetables) {
        this.open = open;
        this.date = date;
        this.meat = meat;
        this.vegetables = vegetables;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public RestoItem[] getMeat() {
        return meat;
    }

    public void setMeat(RestoItem[] meat) {
        this.meat = meat;
    }

    public String[] getVegetables() {
        return vegetables;
    }

    public void setVegetables(String[] vegetables) {
        this.vegetables = vegetables;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
