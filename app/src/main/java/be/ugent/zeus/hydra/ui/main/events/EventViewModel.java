package be.ugent.zeus.hydra.ui.main.events;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.data.models.association.Event;
import be.ugent.zeus.hydra.repository.Result;
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
    protected LiveData<Result<List<Event>>> provideData() {
        return new EventLiveData(getApplication());
    }

    @Override
    protected void onCleared() {
        super.onCleared();

    }
}