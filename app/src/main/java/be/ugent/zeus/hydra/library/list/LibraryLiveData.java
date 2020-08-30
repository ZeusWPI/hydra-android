package be.ugent.zeus.hydra.library.list;

import android.content.Context;
import android.util.Pair;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.arch.data.RequestLiveData;
import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.favourites.FavouritesRepository;

/**
 * @author Niko Strijbol
 */
class LibraryLiveData extends RequestLiveData<List<Pair<Library, Boolean>>> implements Observer<Integer> {

    private LiveData<Integer> favouriteSource;

    LibraryLiveData(Context context) {
        super(context, makeRequest(context));
    }

    /**
     * Construct the request we want to use. This method is static because we need to call it in the constructor.
     *
     * @param context The context.
     *
     * @return The request.
     */
    private static Request<List<Pair<Library, Boolean>>> makeRequest(Context context) {

        FavouritesRepository repository = Database.get(context).getFavouritesRepository();

        return new LibraryListRequest(context).map(libraryList -> {
            Set<String> favourites = new HashSet<>(repository.getFavouriteIds());
            // We sort favourites first and then faculty libraries and then by name.
            return libraryList.getLibraries().stream()
                    .map(library -> Pair.create(library, favourites.contains(library.getCode())))
                    .sorted(Comparator.comparing(statusExtractor()).reversed()
                            .thenComparing(Comparator.comparing(libraryExtractor().andThen(Library::isFacultyBib)).reversed())
                            .thenComparing(libraryExtractor().andThen(Library::getName))).collect(Collectors.toList());
        });
    }

    private static Function<Pair<Library, Boolean>, Boolean> statusExtractor() {
        return libraryBooleanPair -> libraryBooleanPair.second;
    }

    private static Function<Pair<Library, Boolean>, Library> libraryExtractor() {
        return libraryBooleanPair -> libraryBooleanPair.first;
    }

    @Override
    protected void onActive() {
        super.onActive();
        FavouritesRepository repository = Database.get(getContext()).getFavouritesRepository();
        maybeUnregister();
        favouriteSource = Transformations.distinctUntilChanged(repository.count());
        favouriteSource.observeForever(this);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        maybeUnregister();
    }

    private void maybeUnregister() {
        if (favouriteSource != null) {
            favouriteSource.removeObserver(this);
            favouriteSource = null;
        }
    }

    @Override
    public void onChanged(Integer integer) {
        loadData();
    }
}
