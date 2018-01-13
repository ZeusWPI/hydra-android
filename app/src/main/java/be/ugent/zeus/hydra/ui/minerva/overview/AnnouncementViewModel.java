package be.ugent.zeus.hydra.ui.minerva.overview;

import android.app.Application;
import be.ugent.zeus.hydra.minerva.Announcement;
import be.ugent.zeus.hydra.minerva.Course;
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
    protected AnnouncementLiveData constructDataInstance() {
        Objects.requireNonNull(course, "You must set the course before using the view model.");
        return new AnnouncementLiveData(getApplication(), course);
    }
}