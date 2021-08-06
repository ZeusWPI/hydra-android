/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.feed.HomeFeedFragment;
import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDao;

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
            DismissalDao dismissalDao = Database.get(context).getCardDao();
            dismissalDao.deleteAll();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
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
