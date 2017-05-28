package be.ugent.zeus.hydra.ui.main.schamper;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.data.models.schamper.Article;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.ListRequest;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.requests.SchamperArticlesRequest;
import be.ugent.zeus.hydra.repository.RefreshBroadcast;
import be.ugent.zeus.hydra.repository.RequestRepository;
import be.ugent.zeus.hydra.repository.Result;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SchamperViewModel extends AndroidViewModel {

    private final RequestRepository<List<Article>> requestRepository;
    private LiveData<Result<List<Article>>> articleData;

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

    public void refresh() {
        RefreshBroadcast.broadcastRefresh(getApplication(), true);
    }

    private Request<List<Article>> getRequest() {
        return new ListRequest<>(new CachedRequest<>(getApplication(), new SchamperArticlesRequest(), false));
    }
}