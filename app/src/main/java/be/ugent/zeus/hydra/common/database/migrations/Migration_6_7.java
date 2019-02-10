package be.ugent.zeus.hydra.common.database.migrations;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.migration.Migration;
import androidx.annotation.NonNull;
import android.util.Log;

/**
 * An older migration, kept for compatibility reasons.
 *
 * @author Niko Strijbol
 */
public class Migration_6_7 extends Migration {

    public Migration_6_7() {
        super(6, 7);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {

        Log.i("Migrations", "Migrating database from " + this.startVersion + " to " + this.endVersion);

        /* -----------------------------------------
         * Upgrade the database structure
         * ----------------------------------------- */
        // Add column
        supportSQLiteDatabase.execSQL("ALTER TABLE minerva_agenda ADD COLUMN calendar_id INTEGER");
        // Add data
        supportSQLiteDatabase.execSQL("UPDATE minerva_agenda SET calendar_id =-1");
    }
}