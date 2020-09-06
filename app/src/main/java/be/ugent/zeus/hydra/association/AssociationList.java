package be.ugent.zeus.hydra.association;

import android.util.Pair;
import androidx.annotation.NonNull;

import java.util.*;
import java.util.function.Function;

import com.squareup.moshi.Json;

/**
 * A list of associations.
 *
 * @author Niko Strijbol
 */
public final class AssociationList {

    @Json(name = "associations")
    private List<Association> associations;

    public AssociationList() {
    }

    @NonNull
    public List<Association> getAssociations() {
        if (associations == null) {
            return Collections.emptyList();
        } else {
            return associations;
        }
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
