package be.ugent.zeus.hydra.common.caching;

import be.ugent.zeus.hydra.BuildConfig;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import java.io.Serializable;

/**
 * Wrapper object for a {@link Cache} to save additional metadata. This object is serializable, to enable use
 * in file based caching. A side effect of this is that the encapsulated value also must be serializable.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("WeakerAccess")
class CacheObject<T extends Serializable> implements Serializable {

    private final long lastUpdated;
    private final T data;
    private final int version;

    /**
     * Create a new object.
     *
     * @param data The data to save.
     */
    public CacheObject(T data) {
        this.lastUpdated = Instant.now().toEpochMilli();
        this.data = data;
        this.version = BuildConfig.VERSION_CODE;
    }

    CacheObject(long lastUpdated, T data, int version) {
        this.lastUpdated = lastUpdated;
        this.data = data;
        this.version = version;
    }

    /**
     * @return When the data was cached.
     */
    public Instant getLastUpdated() {
        return Instant.ofEpochMilli(this.lastUpdated);
    }

    /**
     * Check if the data is expired. Data is expired when it was cached longer ago than the given duration.
     *
     * @param duration The duration.
     *
     * @return True if the data is expired.
     */
    public boolean isExpired(Duration duration) {
        // Sometimes null is cached, so null is always expired.
        return data == null || getLastUpdated().plus(duration).isBefore(Instant.now());
    }

    /**
     * @return The data.
     */
    public T getData() {
        return data;
    }

    /**
     * @return The app version this data was cached.
     */
    public int getVersion() {
        return version;
    }
}