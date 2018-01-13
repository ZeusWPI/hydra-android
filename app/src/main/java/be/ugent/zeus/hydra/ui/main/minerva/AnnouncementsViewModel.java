package be.ugent.zeus.hydra.ui.main.minerva;

import android.app.Application;
import be.ugent.zeus.hydra.minerva.Announcement;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class AnnouncementsViewModel extends RefreshViewModel<List<Announcement>> {

    public AnnouncementsViewModel(Application application) {
        super(application);
    }

    @Override
    protected AnnouncementsLiveData constructDataInstance() {
        return new AnnouncementsLiveData(getApplication());
    }
}