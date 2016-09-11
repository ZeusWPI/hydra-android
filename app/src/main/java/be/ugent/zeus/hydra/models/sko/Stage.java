package be.ugent.zeus.hydra.models.sko;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * A stage at SKO. A stage has a type, and a number of artists.
 *
 * @author Niko Strijbol
 */
public class Stage implements Serializable {

    @SerializedName("stage")
    private String type;
    private List<Artist> artists;

    public String getType() {
        return type;
    }

    public List<Artist> getArtists() {
        return artists;
    }
}
