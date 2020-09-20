package be.ugent.zeus.hydra.resto.salad;

import android.app.Application;
import androidx.annotation.NonNull;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class SaladViewModel extends RequestViewModel<List<SaladBowl>> {

    public SaladViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<List<SaladBowl>> getRequest() {
        return new SaladRequest(getApplication());
    }
}
