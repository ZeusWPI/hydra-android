package be.ugent.zeus.hydra.ui.main.minerva;

import android.app.Application;
import android.util.Pair;

import be.ugent.zeus.hydra.minerva.Course;
import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class MinervaViewModel extends RefreshViewModel<List<Pair<Course, Long>>> {

    public MinervaViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Pair<Course, Long>>>> constructDataInstance() {
        return new CourseLiveData(getApplication());
    }

    public void destroyInstance() {
        onCleared();
    }
}