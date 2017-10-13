package be.ugent.zeus.hydra.data.network.requests.library;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.data.models.library.OpeningHours;
import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import be.ugent.zeus.hydra.repository.Cache;
import be.ugent.zeus.hydra.repository.requests.CacheableRequest;

/**
 * Get the opening hours for one library.
 *
 * @author Niko Strijbol
 */
public class OpeningHoursRequest extends JsonSpringRequest<OpeningHours[]> implements CacheableRequest<OpeningHours[]> {

    private final String libraryCode;

    public OpeningHoursRequest(Library library) {
        super(OpeningHours[].class);
        this.libraryCode = library.getCode();
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return libraryCode + "_opening.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_DAY * 4;
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.LIBRARY_URL + "libraries/" + libraryCode + "/calendar.json";
    }
}