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

package be.ugent.zeus.hydra.library.favourites;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.*;

import java.util.List;

import be.ugent.zeus.hydra.library.Library;

/**
 * Repository for accessing the favourite libraries.
 *
 * @author Niko Strijbol
 */
@Dao
public abstract class FavouriteRepository {

    @Query("SELECT COUNT(*) FROM " + FavouriteTable.TABLE_NAME + " WHERE " + FavouriteTable.Columns.LIBRARY_ID + " = :libraryId")
    abstract LiveData<Integer> findRowsWith(String libraryId);

    @Query("SELECT " + FavouriteTable.Columns.LIBRARY_ID + " FROM " + FavouriteTable.TABLE_NAME)
    public abstract List<String> getFavouriteIds();

    @Query("SELECT * FROM " + FavouriteTable.TABLE_NAME)
    public abstract List<LibraryFavourite> getAll();

    @Query("SELECT count(*) FROM " + FavouriteTable.TABLE_NAME)
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
