package be.ugent.zeus.hydra.ui.main.minerva;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.repository.Result;
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
    protected LiveData<Result<List<Course>>> constructDataInstance() {
        return new CourseLiveData(getApplication());
    }

    public void destroyInstance() {
        onCleared();
    }
}