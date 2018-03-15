package be.ugent.zeus.hydra.resto.sandwich;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SandwichViewModel extends RequestViewModel<List<Sandwich>> {

    public SandwichViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<Sandwich>> getRequest() {
        return Requests.cachedList(getApplication(), new SandwichRequest());
    }
}