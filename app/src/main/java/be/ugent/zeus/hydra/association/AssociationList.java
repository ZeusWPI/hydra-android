package be.ugent.zeus.hydra.association;

import java.util.List;
import java.util.Objects;

import com.squareup.moshi.Json;

/**
 * A list of associations.
 *
 * @author Niko Strijbol
 */
public class AssociationList {

    @Json(name = "associations")
    private List<Association> associations;

    public AssociationList() {
    }

    public List<Association> getAssociations() {
        return associations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssociationList that = (AssociationList) o;
        return associations.equals(that.associations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(associations);
    }
}
