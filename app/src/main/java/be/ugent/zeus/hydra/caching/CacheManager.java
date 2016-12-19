package be.ugent.zeus.hydra.caching;

import android.content.Context;

/**
 * Provide access to the cache instance.
 *
 * @author Niko Strijbol
 */
public class CacheManager {

    private static Cache cache;

    /**
     * Get an instance of the default cache.
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