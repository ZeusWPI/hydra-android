package be.ugent.zeus.hydra.ui.resto;

import android.app.Application;
import be.ugent.zeus.hydra.resto.Sandwich;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Requests;
import be.ugent.zeus.hydra.resto.network.SandwichRequest;
import be.ugent.zeus.hydra.ui.common.RequestViewModel;

import java.util.Arrays;
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
        return Requests.map(Requests.cache(getApplication(), new SandwichRequest()), Arrays::asList);
    }
}