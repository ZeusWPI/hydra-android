package be.ugent.zeus.hydra.requests.library;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.caching.Cache;
import be.ugent.zeus.hydra.models.library.Library;
import be.ugent.zeus.hydra.models.library.OpeningHourList;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * @author Niko Strijbol
 */
public class OpeningHoursRequest extends CacheableRequest<OpeningHourList> {

    private final String libraryCode;

    public OpeningHoursRequest(Library library) {
        super(OpeningHourList.class);
        this.libraryCode = library.getCode();
    }

    public OpeningHoursRequest(String code) {
        super(OpeningHourList.class);
        this.libraryCode = code;
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
        return "http://widgets.lib.ugent.be/libraries/" + libraryCode + "/calendar.json";
    }
}