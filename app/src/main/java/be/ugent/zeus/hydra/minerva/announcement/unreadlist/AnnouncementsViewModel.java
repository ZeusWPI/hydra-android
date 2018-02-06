package be.ugent.zeus.hydra.minerva.announcement.unreadlist;

import android.app.Application;

import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import be.ugent.zeus.hydra.minerva.announcement.Announcement;

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