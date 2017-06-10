package be.ugent.zeus.hydra.ui.resto.menu;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.requests.Result;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class MenuViewModel extends RefreshViewModel<List<RestoMenu>> {


    public MenuViewModel(Application application) {
        super(application);
    }

    @Override
    protected LiveData<Result<List<RestoMenu>>> constructDataInstance() {
        return new MenuLiveData(getApplication());
    }
}
