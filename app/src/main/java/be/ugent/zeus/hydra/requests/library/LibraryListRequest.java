package be.ugent.zeus.hydra.requests.library;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.caching.Cache;
import be.ugent.zeus.hydra.models.library.LibraryList;
import be.ugent.zeus.hydra.requests.common.CacheableRequest;

/**
 * @author Niko Strijbol
 */
public class LibraryListRequest extends CacheableRequest<LibraryList> {

    public LibraryListRequest() {
        super(LibraryList.class);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return "http://widgets.lib.ugent.be/library_groups/all.json";
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