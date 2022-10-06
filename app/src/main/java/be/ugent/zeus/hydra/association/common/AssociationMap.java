/*
 * Copyright (c) 2021 The Hydra authors
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

package be.ugent.zeus.hydra.association.common;

import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.association.Association;

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

    /**
     * Check if the associations was requested to be shown by the request that
     * produced this association map.
     * 
     * @param abbreviation The abbreviation.
     *                     
     * @return True if requested to be shown, false otherwise.
     */
    boolean isRequested(@NonNull String abbreviation);

    /**
     * Combine the {@link #isRequested(String)} data with the association list.
     */
    default List<Pair<Association, Boolean>> getSelectedAssociations() {
        return this.associations()
                .map(a -> Pair.create(a, this.isRequested(a.getAbbreviation())))
                .collect(Collectors.toList());
    }
}
