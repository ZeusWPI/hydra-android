package be.ugent.zeus.hydra.domain.models.sko;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;

import java.io.Serializable;

/**
 * Exhibitor for the Student Village.
 *
 * @author Niko Strijbol
 */
public final class Exhibitor implements Serializable, Parcelable {

    @SerializedName("naam")
    private String name;
    private String logo;
    private String content;

    public String getLogo() {
        return logo;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.logo);
        dest.writeString(this.content);
    }

    public Exhibitor() {
    }

    protected Exhibitor(Parcel in) {
        this.name = in.readString();
        this.logo = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Exhibitor> CREATOR = new Parcelable.Creator<Exhibitor>() {
        @Override
        public Exhibitor createFromParcel(Parcel source) {
            return new Exhibitor(source);
        }

        @Override
        public Exhibitor[] newArray(int size) {
            return new Exhibitor[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exhibitor exhibitor = (Exhibitor) o;
        return Objects.equals(name, exhibitor.name) &&
                Objects.equals(content, exhibitor.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, content);
    }
}