package be.ugent.zeus.hydra.fragments.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import be.ugent.zeus.hydra.models.library.Library;
import be.ugent.zeus.hydra.loaders.changes.PreferenceListener;
import be.ugent.zeus.hydra.requests.RequestAsyncTaskLoader;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.library.SortedLibraryRequest;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class LibraryLoader extends RequestAsyncTaskLoader<Pair<List<Library>, List<Library>>> {

    private PreferenceListener preferenceListener;

    public LibraryLoader(Context context, Request<Pair<List<Library>, List<Library>>> request) {
        super(context, request);
    }

    public static LibraryLoader sortedLibrary(boolean refresh, Context context) {
        return new LibraryLoader(context, new SortedLibraryRequest(context, refresh));
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        // Listen to changes
        SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferenceListener = new PreferenceListener(this, LibraryListFragment.PREF_LIBRARY_FAVOURITES);
        manager.registerOnSharedPreferenceChangeListener(preferenceListener);
    }

    @Override
    protected void onReset() {
        super.onReset();
        if (preferenceListener != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener);
            preferenceListener = null;
        }
    }
}
