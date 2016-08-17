package be.ugent.zeus.hydra.requests.executor;

import android.content.Context;
import android.os.AsyncTask;

import be.ugent.zeus.hydra.cache.CacheRequest;
import be.ugent.zeus.hydra.cache.file.SerializeCache;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.common.RequestFailureException;

import java.io.Serializable;

/**
 * Utility methods relating to executing {@link Request}s.
 *
 * @author Niko Strijbol
 */
public class RequestExecutor {

    /**
     * Execute a request async.
     *
     * @param request The request.
     * @param callback The callback.
     * @param <T> The result.
     * @return The task, should you wish to cancel it.
     */
    public static <T> AsyncTask<Void, Void, T> executeAsync(final Request<T> request, final RequestCallback<T> callback) {

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
    public static <T extends Serializable, R> AsyncTask<Void, Void, R> executeAsync(Context context, final CacheRequest<T, R> request, final RequestCallback<R> callback) {

        final SerializeCache cache = new SerializeCache(context);

        RequestExecutorTask<R> task = new RequestExecutorTask<R>(callback) {
            @Override
            protected R getData() throws RequestFailureException {
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

        private final RequestCallback<T> callback;
        private Throwable throwable;

        protected RequestExecutorTask(RequestCallback<T> callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected T doInBackground(Void... voids) {
            try {
                return getData();
            } catch (RequestFailureException e) {
                throwable = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(T t) {
            if(t != null) {
                callback.receiveData(t);
            } else {
                callback.receiveError(throwable);
            }
        }

        protected abstract T getData() throws RequestFailureException;
    }
}