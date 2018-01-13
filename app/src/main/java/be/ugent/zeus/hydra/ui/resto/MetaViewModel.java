package be.ugent.zeus.hydra.ui.resto;

import android.app.Application;
import be.ugent.zeus.hydra.resto.RestoMeta;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.resto.network.MetaRequest;
import be.ugent.zeus.hydra.ui.common.RequestViewModel;

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
