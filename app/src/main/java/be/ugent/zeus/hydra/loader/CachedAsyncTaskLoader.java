package be.ugent.zeus.hydra.loader;

import android.content.Context;

import be.ugent.zeus.hydra.cache.Cache;
import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.cache.file.SerializeCache;

import java.io.Serializable;

/**
 * Cached task loader. The task loader requires a {@link CacheRequest} that will be executed. This loader uses a
 * {@link SerializeCache} to cache the responses.
 *
 * For more information about the implementation of Loaders see the link below for a detailed guide.
 *
 * The loader has the option to ignore the cache. If set, the cache is ignored for the next request only. This request
 * does save the data in the cache. All subsequent requests will honour the cache again. If you want to ignore the
 * cache again, you need to call {@link #setNextRefresh()}.
 *
 * @param <D> The result of the request. This value is cached, and so it must be Serializable.
 *
 * @author Niko Strijbol
 * @see <a href="http://www.androiddesignpatterns.com/2012/08/implementing-loaders.html">Implementing loaders</a>
 */
public class CachedAsyncTaskLoader<D extends Serializable> extends AbstractAsyncLoader<D> {

    private CacheRequest<D> request;
    private boolean refresh;
    private final Cache cache;

    /**
     * This loader will honour the cache settings of the request.
     *
     * @param request The request to execute.
     * @param context The context.
     */
    public CachedAsyncTaskLoader(CacheRequest<D> request, Context context) {
        this(request, context, false);
    }

    /**
     * This loader has the option to ignore the cache.
     *
     * @param request   The request to execute.
     * @param context   The context.
     * @param freshData If the data should be fresh or maybe cached.
     */
    public CachedAsyncTaskLoader(CacheRequest<D> request, Context context, boolean freshData) {
        super(context);
        this.request = request;
        this.refresh = freshData;
        this.cache = new SerializeCache(context);
    }

    /**
     * Sets the refresh flag. This means the next request will get new data, regardless of the cache.
     */
    public void setNextRefresh() {
        this.refresh = true;
    }

    @Override
    protected D getData() throws LoaderException {
        try {
            D content;
            if (refresh) {
                //Get new data
                content = cache.get(request, Cache.NEVER);
            } else {
                content = cache.get(request);
            }
            this.refresh = false;
            return content;
        } catch (RequestFailureException e) {
            throw new LoaderException(e);
        }
    }
}