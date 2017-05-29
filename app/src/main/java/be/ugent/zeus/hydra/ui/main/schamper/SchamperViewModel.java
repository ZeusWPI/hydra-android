package be.ugent.zeus.hydra.ui.main.schamper;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.data.models.schamper.Article;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.requests.Requests;
import be.ugent.zeus.hydra.data.network.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.RequestRepository;
import be.ugent.zeus.hydra.repository.Result;
import be.ugent.zeus.hydra.repository.data.RefreshLiveData;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SchamperViewModel extends AndroidViewModel {

    private final RequestRepository<List<Article>> requestRepository;
    private LiveData<Result<List<Article>>> articleData;
    private LiveData<Boolean> refreshing;

    public SchamperViewModel(Application application) {
        super(application);
        requestRepository = new RequestRepository<>(application, true);
    }

    public LiveData<Result<List<Article>>> getData() {
        if (articleData == null) {
            articleData = requestRepository.load(getRequest());
        }

        return articleData;
    }

    public LiveData<Boolean> getRefreshing() {
        if (refreshing == null) {
            refreshing = RefreshLiveData.build(getApplication(), getData());
        }

        return refreshing;
    }

    public void refresh() {
        RefreshBroadcast.broadcastRefresh(getApplication(), true);
    }

    private Request<List<Article>> getRequest() {
        return Requests.map(Requests.cache(getApplication(), new SchamperArticlesRequest()), Arrays::asList);
    }
}