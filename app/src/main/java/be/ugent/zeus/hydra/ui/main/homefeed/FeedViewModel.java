package be.ugent.zeus.hydra.ui.main.homefeed;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import be.ugent.zeus.hydra.repository.Result;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;
import be.ugent.zeus.hydra.ui.main.homefeed.content.HomeCard;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class FeedViewModel extends RefreshViewModel<List<HomeCard>> {

    public FeedViewModel(Application application) {
        super(application);
    }

    @Override
    protected LiveData<Result<List<HomeCard>>> constructDataInstance() {
        return new FeedLiveData(getApplication());
    }
}