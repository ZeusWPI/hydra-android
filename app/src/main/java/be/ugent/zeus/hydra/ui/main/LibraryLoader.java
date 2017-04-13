package be.ugent.zeus.hydra.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;

import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.data.network.CachedRequest;
import be.ugent.zeus.hydra.data.network.requests.library.LibraryListRequest;
import be.ugent.zeus.hydra.data.network.requests.library.SortedLibraryRequest;
import be.ugent.zeus.hydra.ui.common.loaders.PreferenceListener;
import be.ugent.zeus.hydra.ui.common.loaders.RequestAsyncTaskLoader;

import java.util.List;

/**
 * @author Niko Strijbol
 */
class LibraryLoader extends RequestAsyncTaskLoader<Pair<List<Library>, List<Library>>> {

    private PreferenceListener preferenceListener;

    LibraryLoader(Context context, boolean refresh) {
        super(context, new SortedLibraryRequest(context, new CachedRequest<>(context, new LibraryListRequest(), refresh)));
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
