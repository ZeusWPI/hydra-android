package be.ugent.zeus.hydra.data.models.association;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.data.models.IgnoreHashEquals;
import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Represents an association registered with the DSA.
 * <p>
 * An association is identified by it's internal name.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
@AutoValue
public abstract class Association implements Parcelable, Serializable {

    static final String IMAGE_URL = "https://zeus.ugent.be/hydra/api/2.0/association/logo/%s.png";

    public static TypeAdapter<Association> typeAdapter(Gson gson) {
        return new AutoValue_Association.GsonTypeAdapter(gson);
    }

    public static Association create(String internal, String full, String display, String parent) {
        return new AutoValue_Association(internal, full, display, parent);
    }

    @SerializedName("internal_name")
    public abstract String internalName();

    @Nullable
    @IgnoreHashEquals
    @SerializedName("full_name")
    public abstract String fullName();

    @Nullable
    @IgnoreHashEquals
    @SerializedName("display_name")
    public abstract String displayName();

    @Nullable
    @IgnoreHashEquals
    @SerializedName("parent_association")
    public abstract String parentAssociation();

    /**
     * @return A name for this association. If a full name is available, that is returned. If not, the display name is.
     */
    public String name() {
        if (fullName() != null) {
            return fullName();
        }
        return displayName();
    }

    /**
     * @return URL of the image of this association.
     */
    public String imageLink() {
        return String.format(IMAGE_URL, internalName().toLowerCase());
    }
}