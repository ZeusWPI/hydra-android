package be.ugent.zeus.hydra.library.favourites;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

import be.ugent.zeus.hydra.library.Library;

/**
 * A library that was marked as a favourite by the user.
 * <p>
 * This will save various things in the database besides the ID, mainly to be able to use the data without having to
 * get the whole list of libraries every time.
 *
 * @author Niko Strijbol
 */
@Entity(tableName = FavouritesTable.TABLE_NAME)
public final class LibraryFavourite {

    /**
     * The name of the library, for displaying purposes. Note that this is not updated when the language changes,
     * but that would be a lot of work for something that doesn't happen a lot.
     */
    @NonNull
    @ColumnInfo(name = FavouritesTable.Columns.LIBRARY_NAME)
    private final String name;

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = FavouritesTable.Columns.LIBRARY_ID)
    private final String code;

    public LibraryFavourite(@NonNull String name, @NonNull String code) {
        this.name = name;
        this.code = code;
    }

    @NonNull
    public static LibraryFavourite from(@NonNull Library library) {
        return new LibraryFavourite(library.getName(), library.getCode());
    }

    @NonNull
    public String getCode() {
        return code;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryFavourite that = (LibraryFavourite) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
