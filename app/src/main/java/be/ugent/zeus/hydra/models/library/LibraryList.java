package be.ugent.zeus.hydra.models.library;

import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class LibraryList implements Serializable {


    private String name;

    @SerializedName("created_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime createdAt;

    @SerializedName("updated_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime updatedAt;

    private List<Library> libraries;

    public String getName() {
        return name;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Library> getLibraries() {
        return libraries;
    }
}