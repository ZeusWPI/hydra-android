package be.ugent.zeus.hydra.association;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
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