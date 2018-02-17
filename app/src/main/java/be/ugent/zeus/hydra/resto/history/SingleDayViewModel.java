package be.ugent.zeus.hydra.resto.history;

import android.app.Application;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import be.ugent.zeus.hydra.resto.RestoMenu;
import org.threeten.bp.LocalDate;

/**
 * @author Niko Strijbol
 */
public class SingleDayViewModel extends RefreshViewModel<RestoMenu> {

    private LocalDate initialDate;

    public SingleDayViewModel(Application application) {
        super(application);
    }

    public void setInitialDate(LocalDate date) {
        this.initialDate = date;
    }

    public void changeDate(LocalDate date) {
        SingleDayLiveData dayLiveData = (SingleDayLiveData) getData();
        dayLiveData.changeDate(date);
    }

    @Override
    protected BaseLiveData<Result<RestoMenu>> constructDataInstance() {
        return new SingleDayLiveData(getApplication(), initialDate);
    }
}