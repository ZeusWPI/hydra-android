package be.ugent.zeus.hydra.models.resto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;

import java.io.Serializable;

/**
 * Represents a meal.
 *
 * @author Niko Strijbol
 * @author Mitch
 */
public final class RestoMeal implements Parcelable, Serializable {

    private String name;
    private String price;
    private MealType type;
    private String kind;

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

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }

    public enum MealType {
        @SerializedName("main")
        MAIN,
        @SerializedName("side")
        SIDE
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.kind);
    }

    protected RestoMeal(Parcel in) {
        this.name = in.readString();
        this.price = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : MealType.values()[tmpType];
        this.kind = in.readString();
    }

    public static final Parcelable.Creator<RestoMeal> CREATOR = new Parcelable.Creator<RestoMeal>() {
        @Override
        public RestoMeal createFromParcel(Parcel source) {
            return new RestoMeal(source);
        }

        @Override
        public RestoMeal[] newArray(int size) {
            return new RestoMeal[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoMeal restoMeal = (RestoMeal) o;
        return Objects.equals(name, restoMeal.name) &&
                Objects.equals(price, restoMeal.price) &&
                type == restoMeal.type &&
                Objects.equals(kind, restoMeal.kind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, type, kind);
    }
}
