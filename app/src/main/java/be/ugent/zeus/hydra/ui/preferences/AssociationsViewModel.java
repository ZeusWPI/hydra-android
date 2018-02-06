package be.ugent.zeus.hydra.ui.preferences;

import android.app.Application;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.network.AssociationsRequest;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class AssociationsViewModel extends RequestViewModel<List<Association>> {

    public AssociationsViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<Association>> getRequest() {
        return Requests.map(Requests.cache(getApplication(), new AssociationsRequest()), Arrays::asList);
    }
}
