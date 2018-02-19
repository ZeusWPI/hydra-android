package be.ugent.zeus.hydra.resto.history;

import android.app.Application;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import be.ugent.zeus.hydra.resto.RestoChoice;
import be.ugent.zeus.hydra.resto.RestoMenu;
import org.threeten.bp.LocalDate;

/**
 * @author Niko Strijbol
 */
public class SingleDayViewModel extends RefreshViewModel<RestoMenu> {

    public SingleDayViewModel(Application application) {
        super(application);
    }

    public void changeDate(LocalDate date) {
        SingleDayLiveData dayLiveData = (SingleDayLiveData) getData();
        dayLiveData.changeDate(date);
    }

    public void changeResto(RestoChoice choice) {
        SingleDayLiveData dayLiveData = (SingleDayLiveData) getData();
        dayLiveData.changeResto(choice);
    }

    @Override
    protected BaseLiveData<Result<RestoMenu>> constructDataInstance() {
        return new SingleDayLiveData(getApplication());
    }
}