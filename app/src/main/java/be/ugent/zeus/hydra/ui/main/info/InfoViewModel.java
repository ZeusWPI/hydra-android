package be.ugent.zeus.hydra.ui.main.info;

import android.app.Application;
import be.ugent.zeus.hydra.data.models.info.InfoItem;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.data.network.requests.InfoRequest;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.ui.common.RequestViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class InfoViewModel extends RequestViewModel<List<InfoItem>> {

    public InfoViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<InfoItem>> getRequest() {
        return Requests.map(Requests.cache(getApplication(), new InfoRequest()), Arrays::asList);
    }
}