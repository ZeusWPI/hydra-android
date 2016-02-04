package be.ugent.zeus.hydra.models.Resto;

/**
 * Created by feliciaan on 15/10/15.
 */
public class RestoItem {
    private String name;
    private String price;
    private boolean recommended;

    public RestoItem() {};

    public RestoItem(String name, String price, boolean recommended) {
        this.name = name;
        this.price = price;
        this.recommended = recommended;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }
}
