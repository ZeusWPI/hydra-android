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
