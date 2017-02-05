package be.ugent.zeus.hydra.requests.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import be.ugent.zeus.hydra.models.library.LibraryList;
import be.ugent.zeus.hydra.fragments.library.LibraryListFragment;
import be.ugent.zeus.hydra.models.library.Library;
import be.ugent.zeus.hydra.requests.common.ProcessableCacheRequest;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import java8.util.Comparators;
import java8.util.Lists;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class SortedLibraryRequest extends ProcessableCacheRequest<LibraryList, Pair<List<Library>, List<Library>>> {

    /**
     * Create a request.
     *
     * @param context       A context. Can be any context, as the application context is taken.
     * @param shouldRefresh Should fresh data be used or not.
     */
    public SortedLibraryRequest(Context context, boolean shouldRefresh) {
        super(context, new LibraryListRequest(), shouldRefresh);
    }

    @NonNull
    @Override
    protected Pair<List<Library>, List<Library>> transform(@NonNull LibraryList data) throws RequestFailureException {
        Lists.sort(data.getLibraries(), Comparators.comparing(Library::getName));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> favourites = preferences.getStringSet(LibraryListFragment.PREF_LIBRARY_FAVOURITES, Collections.emptySet());

        if (favourites.isEmpty()) {
            return new Pair<>(data.getLibraries(), Collections.emptyList());
        } else {
            List<Library> list = StreamSupport.stream(data.getLibraries())
                    .filter(l -> favourites.contains(l.getCode()))
                    .collect(Collectors.toList());

            return new Pair<>(data.getLibraries(), list);
        }
    }
}