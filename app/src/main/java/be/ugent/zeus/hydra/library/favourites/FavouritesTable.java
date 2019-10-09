package be.ugent.zeus.hydra.library.favourites;

/**
 * Contract for the table with favourite libraries. This represents the current table and column names. These might
 * changes, so you CANNOT use this class in migrations.
 *
 * For a description of the fields, see {@link LibraryFavourite}.
 *
 * @author Niko Strijbol
 */
public final class FavouritesTable {

    private FavouritesTable() {
        // No instances.
    }

    public static final String TABLE_NAME = "library_favourites";

    public interface Columns {
        String LIBRARY_ID = "id";
        String LIBRARY_NAME = "name";
    }
}
