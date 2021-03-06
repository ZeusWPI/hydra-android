package be.ugent.zeus.hydra.info;

import android.app.Application;
import androidx.annotation.NonNull;

import java.util.List;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * @author Niko Strijbol
 */
public class InfoViewModel extends RequestViewModel<List<InfoItem>> {

    public InfoViewModel(Application application) {
        super(application);
    }

    @NonNull
    @Override
    protected Request<List<InfoItem>> getRequest() {
        return new InfoRequest(getApplication());
    }
}