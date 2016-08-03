package be.ugent.zeus.hydra.loader.requests;

import java.io.Serializable;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.loader.cache.CacheRequest;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.loader.cache.file.SerializeCache;

/**
 * @author Niko Strijbol
 */
public class RequestExecutor {

    /**
     * Execute a request async.
     *
     * @param request The request.
     * @param callBack The callback.
     * @param <T> The result.
     * @return The task, should you wish to cancel it.
     */
    public static <T> AsyncTask<Void, Void, T> executeAsync(final Request<T> request, final Callback<T> callBack) {

        AsyncTask<Void, Void, T> task = new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... voids) {
                try {
                    return request.performRequest();
                } catch (RequestFailureException e) {
                    callBack.receiveError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(T t) {
                if(t != null) {
                    callBack.receiveData(t);
                }
            }
        };

        return task.execute();
    }

    public interface Callback<T> {

        void receiveData(@NonNull T data);

        void receiveError(RequestFailureException e);
    }

    /**
     * Execute a cached request async.
     *
     * @param request The request.
     * @param callBack The callback.
     * @param <T> The result.
     * @return The task, should you wish to cancel it.
     */
    public static <T extends Serializable> AsyncTask<Void, Void, T> executeAsync(Context context, final CacheRequest<T> request, final Callback<T> callBack) {

        final SerializeCache cache = new SerializeCache(context);

        AsyncTask<Void, Void, T> task = new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... voids) {
                try {
                    return cache.get(request);
                } catch (RequestFailureException e) {
                    callBack.receiveError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(T t) {
                if(t != null) {
                    callBack.receiveData(t);
                }
            }
        };

        return task.execute();
    }
}