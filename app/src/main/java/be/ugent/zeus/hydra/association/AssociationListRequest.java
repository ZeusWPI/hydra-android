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

package be.ugent.zeus.hydra.association;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import be.ugent.zeus.hydra.common.request.Request;

/**
 * Request to get all associations registered with the DSA.
 *
 * @author Niko Strijbol
 * @author feliciaan
 */
public class AssociationListRequest {

    @NonNull
    public static Request<AssociationMap> create(@NonNull Context context) {
        return new RawRequest(context)
                .map(Mapper::new);
    }

    @VisibleForTesting
    static class RawRequest extends JsonOkHttpRequest<AssociationList> {

        private static final String FILENAME = "verenigingen";

        RawRequest(@NonNull Context context) {
            super(context, AssociationList.class);
        }

        @NonNull
        @Override
        protected String getAPIUrl() {
            return Endpoints.DSA_V4 + FILENAME;
        }

        @Override
        public Duration getCacheDuration() {
            return ChronoUnit.WEEKS.getDuration().multipliedBy(4);
        }
    }

    private static class Mapper implements AssociationMap {

        private final Map<String, Association> associationMap;

        private Mapper(@NonNull AssociationList list) {
            this.associationMap = list.getAssociations()
                    .stream()
                    .collect(Collectors.toMap(Association::getAbbreviation, Function.identity()));
        }

        @NonNull
        @Override
        public Association get(@Nullable String abbreviation) {
            return associationMap.computeIfAbsent(abbreviation, Association::unknown);
        }

        @Override
        public Stream<Association> associations() {
            return associationMap.values().stream();
        }
    }
}