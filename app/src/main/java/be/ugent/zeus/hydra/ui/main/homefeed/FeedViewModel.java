package be.ugent.zeus.hydra.ui.main.homefeed;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;

import be.ugent.zeus.hydra.domain.models.feed.Card;
import be.ugent.zeus.hydra.repository.data.BaseLiveData;
import be.ugent.zeus.hydra.repository.data.SingleLiveEvent;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.ui.common.RefreshViewModel;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class FeedViewModel extends RefreshViewModel<List<Card>> {

    private final SingleLiveEvent<Runnable> commandLiveEvent = new SingleLiveEvent<>();

    public FeedViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Card>>> constructDataInstance() {
        return new FeedLiveData(getApplication());
    }

    public MutableLiveData<Runnable> getCommandLiveEvent() {
        return commandLiveEvent;
    }
}