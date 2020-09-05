package be.ugent.zeus.hydra.association.list;

import android.app.Application;
import android.util.Pair;

import java.util.List;

import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;

/**
 * @author Niko Strijbol
 */
public class EventViewModel extends RefreshViewModel<Pair<List<EventItem>, List<Association>>> {

    private Filter filter;

    public EventViewModel(Application application) {
        super(application);
    }

    public void setParams(Filter filter) {
        this.filter = filter;
    }

    @Override
    protected BaseLiveData<Result<Pair<List<EventItem>, List<Association>>>> constructDataInstance() {
        return new EventLiveData(getApplication(), filter);
    }
}