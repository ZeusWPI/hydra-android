package be.ugent.zeus.hydra.models.sko;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Exhibitor for the Student Village.
 *
 * @author Niko Strijbol
 */
public class Exhibitor implements Serializable {

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
}