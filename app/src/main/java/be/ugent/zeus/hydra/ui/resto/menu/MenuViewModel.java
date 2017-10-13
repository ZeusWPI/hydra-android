package be.ugent.zeus.hydra.ui.resto.menu;

import android.app.Application;
import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
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
    protected BaseLiveData<Result<List<RestoMenu>>> constructDataInstance() {
        return new MenuLiveData(getApplication());
    }
}
