package be.ugent.zeus.hydra.data.network.caching;

import android.content.Context;
import be.ugent.zeus.hydra.repository.Cache;

/**
 * Provide access to the cache instance.
 *
 * @author Niko Strijbol
 */
public class CacheManager {

    private static Cache cache;

    /**
     * Get an instance of the default cache. When called multiple times, the same instance will be returned. This
     * method is thread-safe.
     *
     * @param context A context.
     *
     * @return The default cache.
     */
    public static synchronized Cache defaultCache(Context context) {
        if (cache == null) {
            cache = new GenericCache(context);
        }

        return cache;
    }
}