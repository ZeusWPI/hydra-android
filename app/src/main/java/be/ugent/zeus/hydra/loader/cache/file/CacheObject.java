package be.ugent.zeus.hydra.loader.cache.file;

import be.ugent.zeus.hydra.BuildConfig;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.io.Serializable;

/**
 * This is the actual object that is cached by the cache. It provides the date an object was cached.
 *
 * @see FileCache
 *
 * @author Niko Strijbol
 */
public class CacheObject<T extends Serializable> implements Serializable {

    private DateTime lastUpdated;
    private T data;
    private int version = BuildConfig.VERSION_CODE;

    public CacheObject(T data) {
        this.lastUpdated = DateTime.now();
        this.data = data;
    }

    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    public boolean isExpired(Duration duration) {
        return lastUpdated.plus(duration).isBeforeNow();
    }

    public T getData() {
        return data;
    }

    public int getVersion() {
        return version;
    }
}