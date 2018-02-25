package be.ugent.zeus.hydra.minerva.announcement.courselist;

import android.app.Application;

import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;
import be.ugent.zeus.hydra.minerva.course.Course;
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
    protected LiveData constructDataInstance() {
        Objects.requireNonNull(course, "You must set the course before using the view model.");
        return new LiveData(getApplication(), course);
    }
}