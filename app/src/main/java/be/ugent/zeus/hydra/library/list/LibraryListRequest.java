package be.ugent.zeus.hydra.library.list;

import android.content.Context;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;

/**
 * Get the list of libraries.
 *
 * @author Niko Strijbol
 */
class LibraryListRequest extends JsonOkHttpRequest<LibraryList> {

    LibraryListRequest(Context context) {
        super(context, LibraryList.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.LIBRARY + "library_groups/all.json";
    }

    @Override
    public Duration getCacheDuration() {
        return ChronoUnit.MONTHS.getDuration();
    }
}