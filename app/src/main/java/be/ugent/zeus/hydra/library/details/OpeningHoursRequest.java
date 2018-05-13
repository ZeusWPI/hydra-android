package be.ugent.zeus.hydra.library.details;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.library.Library;
import org.threeten.bp.Duration;

/**
 * Get the opening hours for one library.
 *
 * @author Niko Strijbol
 */
class OpeningHoursRequest extends JsonArrayRequest<OpeningHours> {

    private final String libraryCode;

    OpeningHoursRequest(Context context, Library library) {
        super(context, OpeningHours.class);
        this.libraryCode = library.getCode();
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofDays(4);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.LIBRARY + "libraries/" + libraryCode + "/calendar.json";
    }
}