package be.ugent.zeus.hydra.common.database.migrations;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * This migration adds support for storing favourite libraries in the database instead of preferences.
 *
 * @author Niko Strijbol
 */
public class Migration_15_16 extends Migration {

    private static final String TAG = "Migration_15_16";

    public Migration_15_16() {
        super(15, 16);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {

        Log.i(TAG, "Migrating database from " + this.startVersion + " to " + this.endVersion);

        // We just need to create the new table, nothing else.
        // We don't recover existing data, since that is not possible, nor is it really necessary.
        database.execSQL(
                "CREATE TABLE `library_favourites` (`id` TEXT PRIMARY KEY NOT NULL, `name` TEXT NOT NULL)"
        );

        Log.i(TAG, "Migration completed.");
    }
}
