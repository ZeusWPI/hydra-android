package be.ugent.zeus.hydra.ui.resto;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;
import be.ugent.zeus.hydra.resto.RestoMeta;
import be.ugent.zeus.hydra.resto.network.MetaRequest;

/**
 * @author Niko Strijbol
 */
public class MetaViewModel extends RequestViewModel<RestoMeta> {

    public MetaViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<RestoMeta> getRequest() {
        return Requests.cache(getApplication(), new MetaRequest());
    }
}
