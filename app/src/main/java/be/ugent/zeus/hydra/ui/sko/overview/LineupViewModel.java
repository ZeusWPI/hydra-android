package be.ugent.zeus.hydra.ui.sko.overview;

import android.app.Application;
import be.ugent.zeus.hydra.sko.Artist;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.sko.LineupRequest;
import be.ugent.zeus.hydra.ui.common.RequestViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class LineupViewModel extends RequestViewModel<List<Artist>> {

    public LineupViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<Artist>> getRequest() {
        return Requests.map(Requests.cache(getApplication(), new LineupRequest()), Arrays::asList);
    }
}
