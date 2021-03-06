package be.ugent.zeus.hydra.resto.meta.selectable;

import android.app.Application;
import androidx.annotation.NonNull;

import java.util.List;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;
import be.ugent.zeus.hydra.resto.RestoChoice;

/**
 * @author Niko Strijbol
 */
public class SelectableMetaViewModel extends RequestViewModel<List<RestoChoice>> {

    public SelectableMetaViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<List<RestoChoice>> getRequest() {
        return new SelectableMetaRequest(getApplication());
    }
}
