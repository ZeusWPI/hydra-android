package be.ugent.zeus.hydra.sko.lineup;

import android.app.Application;

import java.util.List;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

/**
 * This class cannot be package private due to technical limitations.
 *
 * @author Niko Strijbol
 */
public class LineupViewModel extends RequestViewModel<List<Artist>> {

    public LineupViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<Artist>> getRequest() {
        return new LineupRequest(getApplication());
    }
}