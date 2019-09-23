package be.ugent.zeus.hydra.resto.sandwich.regular;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class RegularViewModel extends RequestViewModel<List<RegularSandwich>> {

    public RegularViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<RegularSandwich>> getRequest() {
        return new RegularRequest(getApplication());
    }
}
