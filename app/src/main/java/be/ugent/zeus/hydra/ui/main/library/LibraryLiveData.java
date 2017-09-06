package be.ugent.zeus.hydra.ui.main.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.data.models.library.LibraryList;
import be.ugent.zeus.hydra.data.network.requests.library.LibraryListRequest;
import be.ugent.zeus.hydra.repository.data.RequestLiveData;
import be.ugent.zeus.hydra.repository.requests.Request;
import be.ugent.zeus.hydra.repository.requests.Requests;
import java8.util.Comparators;
import java8.util.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class LibraryLiveData extends RequestLiveData<List<Library>> implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Set<String> favouriteLibraries;

    public LibraryLiveData(Context context) {
        super(context, makeRequest(context));
    }

    @Override
    protected void onActive() {
        super.onActive();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.registerOnSharedPreferenceChangeListener(this);
        Set<String> current = preferences.getStringSet(LibraryListFragment.PREF_LIBRARY_FAVOURITES, Collections.emptySet());
        if (favouriteLibraries != null && !current.equals(favouriteLibraries)) {
            loadData(Bundle.EMPTY);
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
            loadData(Bundle.EMPTY);
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

        Request<LibraryList> cached = Requests.cache(context, new LibraryListRequest());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return Requests.map(cached, libraryList -> {
            Set<String> favourites = preferences.getStringSet(LibraryListFragment.PREF_LIBRARY_FAVOURITES, Collections.emptySet());

            List<Library> libraries = libraryList.getLibraries();
            libraries.forEach(library -> library.setFavourite(favourites.contains(library.getCode())));
            Lists.sort(libraries, Comparators.thenComparing(
                    Comparators.reversed(Comparators.comparing(Library::isFavourite)),
                    Comparators.comparing(Library::getName)
            ));

            return libraries;
        });
    }
}