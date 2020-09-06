package be.ugent.zeus.hydra.association.list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.AssociationMap;

/**
 * @author Niko Strijbol
 */
public class MemoryAssociationMap implements AssociationMap {

    private final Map<String, Association> map = new HashMap<>();

    public MemoryAssociationMap() {
    }

    public MemoryAssociationMap(Iterable<Association> associations) {
        for (Association association : associations) {
            map.put(association.getAbbreviation(), association);
        }
    }

    @NonNull
    @Override
    public Association get(@Nullable String abbreviation) {
        return map.getOrDefault(abbreviation, Association.unknown(abbreviation));
    }

    @Override
    public Stream<Association> associations() {
        return map.values().stream();
    }
}
