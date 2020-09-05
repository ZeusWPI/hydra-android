package be.ugent.zeus.hydra.resto.sandwich.regular;

import android.app.Application;
import androidx.annotation.NonNull;

import java.util.List;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class RegularViewModel extends RequestViewModel<List<RegularSandwich>> {

    public RegularViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<List<RegularSandwich>> getRequest() {
        return new RegularRequest(getApplication());
    }
}
