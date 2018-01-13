package be.ugent.zeus.hydra.library;

import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.common.caching.Cache;
import be.ugent.zeus.hydra.repository.requests.CacheableRequest;

/**
 * Get the list of libraries.
 *
 * @author Niko Strijbol
 */
public class LibraryListRequest extends JsonSpringRequest<LibraryList> implements CacheableRequest<LibraryList> {

    public LibraryListRequest() {
        super(LibraryList.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.LIBRARY_URL + "library_groups/all.json";
    }

    @NonNull
    @Override
    public String getCacheKey() {
        return "all_libraries.json";
    }

    @Override
    public long getCacheDuration() {
        return Cache.ONE_WEEK * 4;
    }
}