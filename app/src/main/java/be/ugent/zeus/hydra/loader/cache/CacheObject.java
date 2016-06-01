package be.ugent.zeus.hydra.loader.cache;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.io.Serializable;

/**
 * @author Niko Strijbol
 * @version 1/06/2016
 */
public class CacheObject<T extends Serializable> implements Serializable {

    private DateTime lastUpdated;
    private T data;

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
}