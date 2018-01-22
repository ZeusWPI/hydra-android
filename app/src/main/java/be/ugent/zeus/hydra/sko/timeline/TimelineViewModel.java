package be.ugent.zeus.hydra.sko.timeline;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.request.Requests;
import be.ugent.zeus.hydra.ui.common.RequestViewModel;

import java.util.Arrays;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class TimelineViewModel extends RequestViewModel<List<TimelinePost>> {

    public TimelineViewModel(Application application) {
        super(application);
    }

    @Override
    protected Request<List<TimelinePost>> getRequest() {
        return Requests.map(Requests.cache(getApplication(), new TimelineRequest()), Arrays::asList);
    }
}