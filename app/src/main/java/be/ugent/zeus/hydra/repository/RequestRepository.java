package be.ugent.zeus.hydra.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.repository.data.ModelLiveData;
import be.ugent.zeus.hydra.repository.data.RefreshingLiveData;

/**
 * @author Niko Strijbol
 */
public class RequestRepository<M> implements Repository<M> {

    private final Context context;
    private boolean supportsRefresh = true;

    public RequestRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public RequestRepository(Context context, boolean supportsRefresh) {
        this(context);
        this.supportsRefresh = supportsRefresh;
    }

    @Override
    public LiveData<Result<M>> load(Request<M> request) {
        if (supportsRefresh) {
            return new RefreshingLiveData<>(context, request);
        } else {
            return new ModelLiveData<>(context, request);
        }
    }
}