package be.ugent.zeus.hydra.association;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.stream.Stream;

/**
 * Represents a mapping of association abbreviation to their object.
 *
 * @author Niko Strijbol
 */
public interface AssociationMap {

    /**
     * Get the association linked to the specified mapping.
     * <p>
     * If the abbreviation is not known, or the abbreviation is {@code null},
     * a default "unknown" association will be returned.
     *
     * @param abbreviation The abbreviation.
     * @return The association.
     */
    @NonNull
    Association get(@Nullable String abbreviation);

    /**
     * @return A stream of all known associations.
     */
    Stream<Association> associations();
}
