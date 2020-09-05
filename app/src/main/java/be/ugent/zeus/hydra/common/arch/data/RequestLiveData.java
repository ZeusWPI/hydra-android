package be.ugent.zeus.hydra.common.arch.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * Live data for a {@link Request}.
 *
 * @author Niko Strijbol
 */
public class RequestLiveData<M> extends BaseLiveData<Result<M>> {

    private final Request<M> request;
    private final Context applicationContext;

    public RequestLiveData(Context context, Request<M> request) {
        this(context, request, true);
    }

    public RequestLiveData(Context context, Request<M> request, boolean load) {
        this.applicationContext = context.getApplicationContext();
        this.request = request;
        if (load) {
            loadData();
        }
    }

    /**
     * Load the actual data.
     *
     * @param bundle The arguments for the request.
     */
    @Override
    @SuppressLint("StaticFieldLeak")
    protected void loadData(@NonNull Bundle bundle) {
        new AsyncTask<Void, Void, Result<M>>() {

            @Override
            protected Result<M> doInBackground(Void... voids) {
                return getRequest().execute(bundle);
            }

            @Override
            protected void onPostExecute(Result<M> m) {
                setValue(m);
            }
        }
                .execute();
    }

    protected Context getContext() {
        return applicationContext;
    }

    protected Request<M> getRequest() {
        return request;
    }
}