package be.ugent.zeus.hydra.loader.tasks;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import be.ugent.zeus.hydra.loader.NetworkRequest;
import be.ugent.zeus.hydra.loader.cache.exceptions.RequestFailureException;

/**
 * @author Niko Strijbol
 * @version 4/07/2016
 */
public class NetworkTaskExecutor {

    public static <T> void executeAsync(final NetworkRequest<T> request, final NetworkCallBack<T> callBack) {

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

        task.execute();
    }

    public interface NetworkCallBack<T> {

        void receiveData(@NonNull T data);

        void receiveError(RequestFailureException e);
    }
}