package be.ugent.zeus.hydra.caching;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.*;

/**
 * Cache executor that uses default Java serialization to write/read objects.
 *
 * On Android, the default serializer is not fast. However, for the current use in the application (save some 'smaller'
 * data), it is sufficient. It is also executed in a background thread, so worst case scenario, the user has to wait a
 * little longer for the data (ns or ms). The alternative would be to use an external serializer library
 * (such as fst[1]). This makes the app take up a lot more space, so we do not do that currently.
 * If profiling suggests the serialisation here is really the bottleneck, which is unlikely since
 * it is about network requests, we can easily switch to fst.
 *
 * @author Niko Strijbol
 */
class SerializableExecutor implements CacheExecutor {

    private static final String TAG = "SerializableExecutor";

    private final File directory;

    public SerializableExecutor(File directory) {
        this.directory = directory;
    }

    @Override
    public <D extends Serializable> void save(String key, CacheObject<D> data) throws CacheException {
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(new File(directory, key)));
            stream.writeObject(data);
        } catch (IOException e) {
            throw new CacheException(e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                Log.w(TAG, "Error while closing stream.", e);
            }
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <D extends Serializable> CacheObject<D> read(String key) throws CacheException {
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(new File(directory, key)));
            return (CacheObject<D>) stream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new CacheException(e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                Log.w(TAG, "Error while closing stream.", e);
            }
        }
    }
}
