package be.ugent.zeus.hydra.feed.preferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.common.arch.data.Event;
import be.ugent.zeus.hydra.common.database.RepositoryFactory;
import be.ugent.zeus.hydra.feed.HomeFeedFragment;
import be.ugent.zeus.hydra.feed.cards.CardRepository;

/**
 * Manages events to show toasts when the list of hidden cards is cleared.
 *
 * @author Niko Strijbol
 */
public class DeleteViewModel extends AndroidViewModel {

    private static final String TAG = "DeleteViewModel";

    private final MutableLiveData<Event<Context>> deleteLiveData = new MutableLiveData<>();

    public DeleteViewModel(Application application) {
        super(application);
    }

    void deleteAll() {
        AsyncTask.execute(() -> {
            Context context = getApplication().getApplicationContext();
            CardRepository cardRepository = RepositoryFactory.getCardRepository(context);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            cardRepository.deleteAll();
            boolean newValue = !preferences.getBoolean(HomeFeedFragment.PREF_DISABLED_CARD_HACK, true);
            preferences.edit().putBoolean(HomeFeedFragment.PREF_DISABLED_CARD_HACK, newValue).apply();
            Log.i(TAG, "All hidden cards removed.");
            deleteLiveData.postValue(new Event<>(context));
        });
    }

    LiveData<Event<Context>> getLiveData() {
        return deleteLiveData;
    }
}
