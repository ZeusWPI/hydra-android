package be.ugent.zeus.hydra.ui.resto;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;
import be.ugent.zeus.hydra.resto.network.SelectableMetaRequest;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SelectableMetaViewModel extends RequestViewModel<List<SelectableMetaRequest.RestoChoice>> {

    public SelectableMetaViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<SelectableMetaRequest.RestoChoice>> getRequest() {
        return new SelectableMetaRequest(getApplication());
    }
}
