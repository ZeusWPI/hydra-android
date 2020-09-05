package be.ugent.zeus.hydra.association.event.list;

import android.app.Application;

import java.util.List;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;

/**
 * @author Niko Strijbol
 */
public class EventViewModel extends RefreshViewModel<List<EventItem>> {

    public EventViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<EventItem>>> constructDataInstance() {
        return new EventLiveData(getApplication());
    }
}