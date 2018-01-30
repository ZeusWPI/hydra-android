package be.ugent.zeus.hydra.minerva.ui.calendaritem;

import android.app.Application;
import be.ugent.zeus.hydra.minerva.AgendaItem;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

/**
 * @author Niko Strijbol
 */
public class AgendaViewModel extends RefreshViewModel<AgendaItem> {

    private int id;

    public AgendaViewModel(Application application) {
        super(application);
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    protected BaseLiveData<Result<AgendaItem>> constructDataInstance() {
        return new AgendaLiveData(getApplication(), id);
    }
}