package be.ugent.zeus.hydra.ui.main.minerva;

import android.app.Application;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.network.requests.Result;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class CourseViewModel extends RefreshViewModel<List<Course>> {

    public CourseViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Course>>> constructDataInstance() {
        return new CourseLiveData(getApplication());
    }

    public void destroyInstance() {
        onCleared();
    }
}