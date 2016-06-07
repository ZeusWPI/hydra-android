package be.ugent.zeus.hydra.loader.cache.file;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.zeus.hydra.loader.cache.exceptions.CacheException;

import java.io.*;

/**
 * File cache that serializes the data.
 *
 * This class uses default serialization to save the objects. On Android, the default serializer is not fast. However,
 * for the current use in the application (save some 'smaller' data), it is sufficient. It is also executed in a
 * background thread, so worst case scenario, the user has to wait a little longer for the data (ns or ms). The
 * alternative would be to use an external serializer library (such as fst[1]). This makes the app take up a lot more
 * space, so we do not do that currently. If profiling suggests the serialisation here is really the bottleneck, which
 * is unlikely since it is about network requests, we can easily switch to fst.-
 *
 * @see [1] <a href="https://github.com/RuedigerMoeller/fast-serialization">fst</a>
 *
 * @author Niko Strijbol
 */
public class SerializeCache extends FileCache {

    public SerializeCache(Context context) {
        super(context);
    }

    /**
     * Write an object as JSON. The built in Gson writer does not work on android.
     *
     * @param data The data to write.
     * @throws CacheException
     */
    protected <T extends Serializable> void write(String name, CacheObject<T> data) throws CacheException {
        ObjectOutputStream stream = null;
        try {
            stream = new ObjectOutputStream(new FileOutputStream(new File(directory, name)));
            stream.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error while closing stream", e);
            }
        }
    }

    /**
     * Read data from the cache.
     *
     * @param name Name of the file.
     * @param <T> The type of the CacheObject.
     * @return The CacheObject.
     * @throws CacheException If the file was not found.
     */
    @NonNull
    @SuppressWarnings("unchecked")
    protected <T extends Serializable> CacheObject<T> read(String name) throws CacheException {
        ObjectInputStream stream = null;
        try {
            stream = new ObjectInputStream(new FileInputStream(new File(directory, name)));
            return (CacheObject<T>) stream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new CacheException(e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error while closing stream", e);
            }
        }
    }
}