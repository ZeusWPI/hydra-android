package be.ugent.zeus.hydra.ui.main.events;

import android.app.Application;
import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class EventViewModel extends RefreshViewModel<List<Event>> {

    public EventViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Event>>> constructDataInstance() {
        return new EventLiveData(getApplication());
    }
}