package be.ugent.zeus.hydra.data.network.requests.library;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;

import be.ugent.zeus.hydra.data.models.library.Library;
import be.ugent.zeus.hydra.data.models.library.LibraryList;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import be.ugent.zeus.hydra.ui.main.LibraryListFragment;
import java8.util.Comparators;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.*;

/**
 * Get all libraries, but sorted and split according to the user's favourites.
 *
 * @author Niko Strijbol
 */
public class SortedLibraryRequest implements Request<Pair<List<Library>, List<Library>>> {

    private final Context context;
    private final Request<LibraryList> request;


    public SortedLibraryRequest(Context context, Request<LibraryList> request) {
        this.context = context.getApplicationContext();
        this.request = request;
    }

    @NonNull
    @Override
    public Pair<List<Library>, List<Library>> performRequest(Bundle args) throws RequestFailureException {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> favourites = preferences.getStringSet(LibraryListFragment.PREF_LIBRARY_FAVOURITES, Collections.emptySet());

        Map<Boolean, List<Library>> split = StreamSupport.stream(request.performRequest(null).getLibraries())
                .sorted(Comparators.comparing(Library::getName))
                .collect(Collectors.partitioningBy(library -> favourites.contains(library.getCode())));

        return new Pair<>(split.get(false), split.get(true));
    }
}