package be.ugent.zeus.hydra.ui.main.resto;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.repository.Result;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

/**
 * @author Niko Strijbol
 */
public class RestoViewModel extends RefreshViewModel<RestoMenu> {

    public RestoViewModel(Application application) {
        super(application);
    }

    @Override
    protected LiveData<Result<RestoMenu>> constructDataInstance() {
        return new RestoLiveData(getApplication());
    }
}