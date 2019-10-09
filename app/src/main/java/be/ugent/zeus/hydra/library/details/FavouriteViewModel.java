package be.ugent.zeus.hydra.library.details;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import be.ugent.zeus.hydra.common.database.Database;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.favourites.FavouritesRepository;

/**
 * @author Niko Strijbol
 */
public class FavouriteViewModel extends AndroidViewModel {

    private Library library;
    private final FavouritesRepository repository;

    public FavouriteViewModel(@NonNull Application application) {
        super(application);
        this.repository = Database.get(application).getFavouritesRepository();
    }

    public void setLibrary(Library library) {
        this.library = library;
    }

    public LiveData<Boolean> getData() {
        return repository.isAsyncFavourite(library.getCode());
    }
}
