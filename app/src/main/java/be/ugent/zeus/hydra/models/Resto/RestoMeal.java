package be.ugent.zeus.hydra.models.Resto;

/**
 * Created by Mitch on 3/03/2016.
 */
public class RestoMeal {

    private String name;
    private String price;
    private String type;
    private String kind;

    public RestoMeal() {}

    public RestoMeal(String name, String price, String type, String kind) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.kind = kind;
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

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
