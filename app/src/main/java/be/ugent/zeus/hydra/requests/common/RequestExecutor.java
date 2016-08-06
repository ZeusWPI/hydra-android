package be.ugent.zeus.hydra.requests.common;

import java.io.Serializable;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.cache.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.cache.file.SerializeCache;

/**
 * Utility methods relating to executing {@link Request}s.
 *
 * @author Niko Strijbol
 */
public class RequestExecutor {

    /**
     * Callback for the request executor.
     *
     * @param <T> The result.
     */
    public interface Callback<T> {

        /**
         * Receive the data if the request was completed successfully.
         *
         * @param data The data.
         */
        void receiveData(@NonNull T data);

        /**
         * Receive an error if the request failed for some reason.
         *
         * @param e The occurred exception.
         */
        void receiveError(@NonNull Throwable e);
    }

    /**
     * Execute a request async.
     *
     * @param request The request.
     * @param callback The callback.
     * @param <T> The result.
     * @return The task, should you wish to cancel it.
     */
    public static <T> AsyncTask<Void, Void, T> executeAsync(final Request<T> request, final Callback<T> callback) {

        RequestExecutorTask<T> task = new RequestExecutorTask<T>(callback) {
            @Override
            protected T getData() throws RequestFailureException {
                return request.performRequest();
            }
        };

        return task.execute();
    }

    /**
     * Execute a cached request async.
     *
     * @param request The request.
     * @param callback The callback.
     * @param <T> The result.
     * @return The task, should you wish to cancel it.
     */
    public static <T extends Serializable> AsyncTask<Void, Void, T> executeAsync(Context context, final CacheRequest<T> request, final Callback<T> callback) {

        final SerializeCache cache = new SerializeCache(context);

        RequestExecutorTask<T> task = new RequestExecutorTask<T>(callback) {
            @Override
            protected T getData() throws RequestFailureException {
                return cache.get(request);
            }
        };

        return task.execute();
    }

    /**
     * Internal AsyncTask used to execute the request.
     * @param <T>
     */
    private static abstract class RequestExecutorTask<T> extends AsyncTask<Void, Void, T> {

        private final Callback<T> callback;

        protected RequestExecutorTask(Callback<T> callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected T doInBackground(Void... voids) {
            try {
                return getData();
            } catch (RequestFailureException e) {
                callback.receiveError(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(T t) {
            if(t != null) {
                callback.receiveData(t);
            } else {
                callback.receiveError(new NullPointerException("The data may not be null."));
            }
        }

        protected abstract T getData() throws RequestFailureException;
    }
}