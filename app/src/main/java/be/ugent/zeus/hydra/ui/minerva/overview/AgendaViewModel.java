package be.ugent.zeus.hydra.ui.minerva.overview;

import android.app.Application;
import be.ugent.zeus.hydra.data.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;
import java8.util.Objects;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class AgendaViewModel extends RefreshViewModel<List<AgendaItem>> {

    private Course course;

    public AgendaViewModel(Application application) {
        super(application);
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    protected BaseLiveData<Result<List<AgendaItem>>> constructDataInstance() {
        Objects.requireNonNull(course, "You must set the course before using the view model.");
        return new AgendaLiveData(getApplication() , course);
    }
}