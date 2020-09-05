package be.ugent.zeus.hydra.association.preference;

import android.app.Application;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.association.AssociationListRequest;
import be.ugent.zeus.hydra.association.AssociationMap;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class AssociationsViewModel extends RequestViewModel<AssociationMap> {

    public AssociationsViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<AssociationMap> getRequest() {
        return AssociationListRequest.create(getApplication());
    }
}
