package be.ugent.zeus.hydra.library.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.library.Library;
import java9.lang.Iterables;
import java9.util.Comparators;
import java9.util.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
class LibraryLiveData extends RequestLiveData<List<Library>> implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Set<String> favouriteLibraries;

    LibraryLiveData(Context context) {
        super(context, makeRequest(context));
    }

    @Override
    protected void onActive() {
        super.onActive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.registerOnSharedPreferenceChangeListener(this);
        Set<String> current = preferences.getStringSet(LibraryListFragment.PREF_LIBRARY_FAVOURITES, Collections.emptySet());
        if (favouriteLibraries != null && !current.equals(favouriteLibraries)) {
            loadData();
        }
        favouriteLibraries = current;
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (LibraryListFragment.PREF_LIBRARY_FAVOURITES.equals(key)) {
            loadData();
        }
    }

    /**
     * Construct the request we want to use. This method is static because we need to call it in the constructor.
     *
     * @param context The context.
     *
     * @return The request.
     */
    private static Request<List<Library>> makeRequest(Context context) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return new LibraryListRequest(context).map(libraryList -> {
            Set<String> favourites = preferences.getStringSet(LibraryListFragment.PREF_LIBRARY_FAVOURITES, Collections.emptySet());

            List<Library> libraries = libraryList.getLibraries();
            Iterables.forEach(libraries, library -> library.setFavourite(favourites.contains(library.getCode())));
            Lists.sort(libraries, Comparators.thenComparing(
                    Comparators.reversed(Comparators.comparing(Library::isFavourite)),
                    Comparators.thenComparing(
                            Comparators.reversed(Comparators.comparing(Library::isFacultyBib)),
                            Comparators.comparing(Library::getName)
            )));

            return libraries;
        });
    }
}