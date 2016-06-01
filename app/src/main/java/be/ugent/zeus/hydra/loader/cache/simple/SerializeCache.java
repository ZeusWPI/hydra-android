package be.ugent.zeus.hydra.loader.cache.simple;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.zeus.hydra.loader.cache.CacheObject;
import be.ugent.zeus.hydra.loader.cache.exceptions.CacheException;

import java.io.*;

/**
 * Simple cache that serializes the data.
 * @author Niko Strijbol
 * @version 1/06/2016
 */
public class SerializeCache extends SimpleCache {

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