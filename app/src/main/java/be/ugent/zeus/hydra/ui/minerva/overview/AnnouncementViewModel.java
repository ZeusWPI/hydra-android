package be.ugent.zeus.hydra.ui.minerva.overview;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.repository.Result;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;
import java8.util.Objects;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class AnnouncementViewModel extends RefreshViewModel<List<Announcement>> {

    private Course course;

    public AnnouncementViewModel(Application application) {
        super(application);
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    protected LiveData<Result<List<Announcement>>> constructDataInstance() {
        Objects.requireNonNull(course, "You must set the course before using the view model.");
        return new AnnouncementLiveData(getApplication(), course);
    }
}
