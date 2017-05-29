package be.ugent.zeus.hydra.repository.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.PartialDataException;
import be.ugent.zeus.hydra.data.network.exceptions.RequestException;
import be.ugent.zeus.hydra.repository.Result;

/**
 * Live data for a {@link Request}.
 *
 * @author Niko Strijbol
 */
public class ModelLiveData<M> extends LiveData<Result<M>> {

    private final Request<M> request;
    private final Context applicationContext;

    public ModelLiveData(Context context, Request<M> model) {
        this.applicationContext = context.getApplicationContext();
        this.request = model;
        init();
    }

    protected void init() {
        loadData(Bundle.EMPTY);
    }

    @Nullable
    @Override
    public Result<M> getValue() {
        return super.getValue();
    }

    /**
     * Load the actual data.
     *
     * @param bundle The arguments for the request.
     */
    protected void loadData(@Nullable Bundle bundle) {
        new AsyncTask<Void, Void, M>() {

            private RequestException exception;

            @Override
            protected M doInBackground(Void... voids) {
                try {
                    return request.performRequest(bundle);
                } catch (RequestException e) {
                    this.exception = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(M m) {
                super.onPostExecute(m);

                Result.Builder<M> builder = Result.Builder.create();
                if (m == null) {
                    builder.withError(exception)
                            .withStatus(Result.Status.ERROR);
                    // Add offline data if available (this is a special case for the cache).
                    if (exception instanceof PartialDataException) {
                        builder.withData(((PartialDataException) exception).getData());
                    }
                } else {
                    builder.withData(m)
                            .withStatus(Result.Status.DONE);
                }

                setValue(builder.build());
            }
        }
        .execute();
    }

    protected Context getContext() {
        return applicationContext;
    }
}