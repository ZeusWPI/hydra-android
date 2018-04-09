package be.ugent.zeus.hydra.resto.meta;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class MetaViewModel extends RequestViewModel<RestoMeta> {

    public MetaViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<RestoMeta> getRequest() {
        return new MetaRequest(getApplication());
    }
}
