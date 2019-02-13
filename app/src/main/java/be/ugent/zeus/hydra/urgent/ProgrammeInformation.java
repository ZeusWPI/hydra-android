package be.ugent.zeus.hydra.urgent;


import com.squareup.moshi.Json;
import java9.util.Objects;

public class ProgrammeInformation {

    private final String name;
    @Json(name = "image")
    private final String imageUrl;
    private final String description;


    public ProgrammeInformation(String name, String imageUrl, String description) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProgrammeInformation that = (ProgrammeInformation) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(imageUrl, that.imageUrl) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, imageUrl, description);
    }
}
