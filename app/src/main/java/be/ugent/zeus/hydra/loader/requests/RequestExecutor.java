package be.ugent.zeus.hydra.loader.requests;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;

/**
 * @author Niko Strijbol
 * @version 4/07/2016
 */
public class RequestExecutor {

    /**
     *
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
}