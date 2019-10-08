package be.ugent.zeus.hydra.library.favourites;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.*;

import java.util.List;
import java.util.Set;

import be.ugent.zeus.hydra.common.arch.data.BaseLiveData;
import be.ugent.zeus.hydra.library.Library;

/**
 * Repository for accessing the favourite libraries.
 *
 * @author Niko Strijbol
 */
@Dao
public abstract class FavouritesRepository {

    @Query("SELECT COUNT(*) FROM " + FavouritesTable.TABLE_NAME + " WHERE " + FavouritesTable.Columns.LIBRARY_ID + " = :libraryId")
    abstract LiveData<Integer> findRowsWith(String libraryId);

    @Query("SELECT " + FavouritesTable.Columns.LIBRARY_ID + " FROM " + FavouritesTable.TABLE_NAME)
    public abstract List<String> getFavouriteIds();

    @Query("SELECT * FROM " + FavouritesTable.TABLE_NAME)
    public abstract List<LibraryFavourite> getAll();

    @Query("SELECT count(*) FROM " + FavouritesTable.TABLE_NAME)
    public abstract LiveData<Integer> count();

    public LiveData<Boolean> isAsyncFavourite(String libraryId) {
        return Transformations.map(findRowsWith(libraryId), input -> input > 0);
    }

    public void delete(Library library) {
        this.delete(LibraryFavourite.from(library));
    }

    public void insert(Library library) {
        this.insert(LibraryFavourite.from(library));
    }

    @Delete
    public abstract void delete(LibraryFavourite favourite);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(LibraryFavourite favourite);
}
