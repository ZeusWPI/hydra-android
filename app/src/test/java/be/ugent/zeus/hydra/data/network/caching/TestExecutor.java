package be.ugent.zeus.hydra.data.network.caching;

import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.BuildConfig;
import org.threeten.bp.Instant;

import java.io.Serializable;

/**
 * Test executor that can track if it was read or not.
 *
 * @author Niko Strijbol
 */
public class TestExecutor implements CacheExecutor {

    private boolean hasSaved;

    private long updated = Instant.now().toEpochMilli();
    private int code = BuildConfig.VERSION_CODE;

    @Override
    public <D extends Serializable> void save(String key, CacheObject<D> data) throws CacheException {
        hasSaved = true;
    }

    @NonNull
    @Override
    public <D extends Serializable> CacheObject<D> read(String key) throws CacheException {

        if (key.equals(TestObject.TEST_FILE_KEY)) {
            //noinspection unchecked
            return new CacheObject<>(updated, (D) new TestObject(), code);
        }

        throw new CacheException("Test: not found.");
    }

    public boolean isHasSaved() {
        return hasSaved;
    }

    void setUpdated(long updated) {
        this.updated = updated;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void reset() {
        hasSaved = false;
    }
}
