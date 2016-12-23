package be.ugent.zeus.hydra.library;

import be.ugent.zeus.hydra.models.converters.BooleanJsonAdapter;
import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class Library implements Serializable {

    private String departement;
    private String email;
    private List<String> address;
    private String name;
    private String code;
    private List<String> telephone;
    @JsonAdapter(BooleanJsonAdapter.class)
    private boolean active;
    @SerializedName("thumbnail_url")
    private String thumbnail;
    @SerializedName("image_url")
    private String image;

    @SerializedName("lat")
    private String latitude;
    @SerializedName("long")
    private String longitude;

    private String contact;

    private String campus;
    private String faculty;
    private String link;

    @SerializedName("created_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime createdAt;

    @SerializedName("updated_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime updatedAt;

    public String getDepartement() {
        return departement;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public List<String> getTelephone() {
        return telephone;
    }

    public boolean isActive() {
        return active;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getImage() {
        return image;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getContact() {
        return contact;
    }

    public String getCampus() {
        return campus;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getLink() {
        return link;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }
}