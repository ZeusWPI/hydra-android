package be.ugent.zeus.hydra.resto.meta;

import android.app.Application;

import androidx.annotation.NonNull;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class MetaViewModel extends RequestViewModel<RestoMeta> {

    public MetaViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<RestoMeta> getRequest() {
        return new MetaRequest(getApplication());
    }
}
