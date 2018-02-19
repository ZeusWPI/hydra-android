package be.ugent.zeus.hydra.resto.meta.selectable;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;
import be.ugent.zeus.hydra.resto.RestoChoice;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SelectableMetaViewModel extends RequestViewModel<List<RestoChoice>> {

    public SelectableMetaViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<RestoChoice>> getRequest() {
        return new SelectableMetaRequest(getApplication());
    }
}
