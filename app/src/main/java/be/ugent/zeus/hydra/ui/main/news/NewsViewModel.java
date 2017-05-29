package be.ugent.zeus.hydra.ui.main.news;

import android.app.Application;
import be.ugent.zeus.hydra.data.models.association.UgentNewsItem;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.requests.Requests;
import be.ugent.zeus.hydra.data.network.requests.association.UgentNewsRequest;
import be.ugent.zeus.hydra.ui.common.RequestViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class NewsViewModel extends RequestViewModel<List<UgentNewsItem>> {

    public NewsViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<UgentNewsItem>> getRequest() {
        return Requests.map(Requests.cache(getApplication(), new UgentNewsRequest()), Arrays::asList);
    }
}