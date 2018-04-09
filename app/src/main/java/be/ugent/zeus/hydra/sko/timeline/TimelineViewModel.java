package be.ugent.zeus.hydra.sko.timeline;

import android.app.Application;

import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.common.ui.RequestViewModel;

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
        return new TimelineRequest(getApplication());
    }
}