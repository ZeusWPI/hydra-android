package be.ugent.zeus.hydra.ui.resto.menu;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import be.ugent.zeus.hydra.data.models.resto.RestoMenu;
import be.ugent.zeus.hydra.data.network.requests.resto.SelectableMetaRequest;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;
import be.ugent.zeus.hydra.ui.common.RequestViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class MenuViewModel extends RefreshViewModel<List<RestoMenu>> {

    private LiveData<Result<List<SelectableMetaRequest.RestoChoice>>> metaLiveData;

    public MenuViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<RestoMenu>>> constructDataInstance() {
        return new MenuLiveData(getApplication());
    }

    public LiveData<Result<List<SelectableMetaRequest.RestoChoice>>> getMetaLiveData() {
        if (metaLiveData == null) {
            RequestViewModel<List<SelectableMetaRequest.RestoChoice>> model = new RequestViewModel<List<SelectableMetaRequest.RestoChoice>>(getApplication()) {
                @Override
                protected Request<List<SelectableMetaRequest.RestoChoice>> getRequest() {
                    return new SelectableMetaRequest(getApplication());
                }
            };
            metaLiveData = model.constructDataInstance();
        }
        return metaLiveData;
    }
}