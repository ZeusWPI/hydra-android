package be.ugent.zeus.hydra.library.list;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.threeten.bp.Duration;
import org.threeten.bp.temporal.ChronoUnit;

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