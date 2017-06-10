package be.ugent.zeus.hydra.ui.minerva;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.data.network.requests.Result;
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
    protected LiveData<Result<AgendaItem>> constructDataInstance() {
        return new AgendaLiveData(getApplication(), id);
    }
}