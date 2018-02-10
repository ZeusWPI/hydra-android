package be.ugent.zeus.hydra.feed;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.common.arch.data.SingleLiveEvent;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import be.ugent.zeus.hydra.feed.cards.Card;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class FeedViewModel extends RefreshViewModel<List<Card>> {

    private SingleLiveEvent<Runnable> commandLiveEvent;

    public FeedViewModel(Application application) {
        super(application);
    }

    @Override
    protected BaseLiveData<Result<List<Card>>> constructDataInstance() {
        return new FeedLiveData(getApplication());
    }

    public MutableLiveData<Runnable> getCommandLiveEvent() {
        if (commandLiveEvent == null) {
            commandLiveEvent = new SingleLiveEvent<>();
        }
        return commandLiveEvent;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        commandLiveEvent = null;
    }
}