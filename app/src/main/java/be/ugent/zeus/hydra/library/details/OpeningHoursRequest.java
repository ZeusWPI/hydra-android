package be.ugent.zeus.hydra.library.details;

import android.content.Context;
import androidx.annotation.NonNull;

import java9.util.Optional;
import java9.util.stream.StreamSupport;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.library.Library;
import org.threeten.bp.Duration;
import org.threeten.bp.LocalDate;

/**
 * Get the opening hours for one library.
 *
 * @author Niko Strijbol
 */
public class OpeningHoursRequest extends JsonArrayRequest<OpeningHours> {

    private final String libraryCode;

    public OpeningHoursRequest(Context context, Library library) {
        this(context, library.getCode());
    }

    public OpeningHoursRequest(Context context, String libraryCode) {
        super(context, OpeningHours.class);
        this.libraryCode = libraryCode;
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

    public Request<Optional<OpeningHours>> forDay(LocalDate date) {
        return map(openingHours -> StreamSupport.stream(openingHours)
                .filter(o -> date.equals(o.getDate()))
                .findFirst());
    }
}