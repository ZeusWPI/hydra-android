package be.ugent.zeus.hydra.ui.main.resto;

import android.app.Application;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import be.ugent.zeus.hydra.resto.RestoMenu;

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