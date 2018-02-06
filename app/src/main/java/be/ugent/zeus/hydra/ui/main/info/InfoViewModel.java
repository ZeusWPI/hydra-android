package be.ugent.zeus.hydra.ui.main.info;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;
import be.ugent.zeus.hydra.info.InfoItem;
import be.ugent.zeus.hydra.info.InfoRequest;

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