package be.ugent.zeus.hydra.resto.sandwich.ecological;

import android.app.Application;

import java.util.List;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class EcologicalViewModel extends RequestViewModel<List<EcologicalSandwich>> {

    public EcologicalViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<EcologicalSandwich>> getRequest() {
        return new EcologicalRequest(getApplication());
    }
}
