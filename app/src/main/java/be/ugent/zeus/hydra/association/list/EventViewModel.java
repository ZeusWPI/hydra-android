package be.ugent.zeus.hydra.association.list;

import android.app.Application;
import android.content.Context;
import android.util.Pair;

import java.util.List;

import be.ugent.zeus.hydra.association.AssociationMap;
import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;

/**
 * @author Niko Strijbol
 */
public class EventViewModel extends RefreshViewModel<Pair<List<EventItem>, AssociationMap>> {

    private Filter filter;

    public EventViewModel(Application application) {
        super(application);
    }

    public void setParams(Filter filter) {
        this.filter = filter;
    }

    @Override
    protected BaseLiveData<Result<Pair<List<EventItem>, AssociationMap>>> constructDataInstance() {
        Context context = getApplication();
        return new RequestLiveData<>(context, EventItem.request(context, filter));
    }
}