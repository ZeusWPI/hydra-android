/*
 * Copyright (c) 2023 Niko Strijbol
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.association;

import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A class representing a map of associations.
 *
 * <p>
 * The AssociationMap class provides methods to access associations based on their abbreviation,
 * check if an association was requested to be shown, and combine the requested associations with
 * the association list.
 * </p>
 */
public class AssociationMap {

    private final Map<String, Association> associationMap;
    private final Set<String> associationRequested;
    
    public AssociationMap() {
        this(Collections.emptyList(), Collections.emptySet());
    }

    public AssociationMap(@NonNull List<Association> list, @NonNull Set<String> requestedAssociations) {
        this.associationMap = new HashMap<>();
        for (var association : list) {
            associationMap.put(association.abbreviation(), association);
        }
        this.associationRequested = requestedAssociations;
    }

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
    public Association get(@Nullable String abbreviation) {
        return associationMap.computeIfAbsent(abbreviation, Association::unknown);
    }

    /**
     * @return A stream of all known associations.
     */
    @NonNull
    public Stream<Association> associations() {
        return associationMap.values().stream();
    }

    /**
     * Check if the association was requested to be shown by the request that
     * produced this association map.
     *
     * @param abbreviation The abbreviation.
     * @return True if requested to be shown, false otherwise.
     */
    public boolean isRequested(@NonNull String abbreviation) {
        return associationRequested.contains(abbreviation);
    }

    /**
     * Combine the {@link #isRequested(String)} data with the association list.
     */
    public List<Pair<Association, Boolean>> requestedAssociations() {
        return this.associations()
                .map(a -> Pair.create(a, this.isRequested(a.abbreviation())))
                .collect(Collectors.toList());
    }
}
