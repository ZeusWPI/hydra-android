package be.ugent.zeus.hydra.ui.main.minerva;

import android.app.Application;
import android.util.Pair;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;
import be.ugent.zeus.hydra.ui.common.recyclerview.ResultStarter;
import java8.util.Objects;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class MinervaViewModel extends RefreshViewModel<List<Pair<Course, Integer>>> {

    private ResultStarter resultStarter;

    public MinervaViewModel(Application application) {
        super(application);
    }

    public void setResultStarter(ResultStarter resultStarter) {
        this.resultStarter = resultStarter;
    }

    @Override
    protected BaseLiveData<Result<List<Pair<Course, Integer>>>> constructDataInstance() {
        return new CourseLiveData(getApplication());
    }

    public void destroyInstance() {
        onCleared();
    }

    public ResultStarter getResultStarter() {
        return Objects.requireNonNull(resultStarter);
    }
}