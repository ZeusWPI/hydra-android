package be.ugent.zeus.hydra.data.database.minerva2.migrations;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.util.Log;

/**
 * Actually adjust the scheme to support Room.
 *
 * The SQL for creating the tables is copied from the scheme exported by Room.
 *
 * @author Niko Strijbol
 */
public class Migration_8_9 extends Migration {

    public Migration_8_9() {
        super(8, 9);
    }

    @Override
    public void migrate(SupportSQLiteDatabase supportSQLiteDatabase) {

        Log.i("Migrations", "Migrating database from " + this.startVersion + " to " + this.endVersion);

        // Add the column
        supportSQLiteDatabase.execSQL("ALTER TABLE minerva_agenda ADD COLUMN is_merged INTEGER NOT NULL DEFAULT 0");
    }
}