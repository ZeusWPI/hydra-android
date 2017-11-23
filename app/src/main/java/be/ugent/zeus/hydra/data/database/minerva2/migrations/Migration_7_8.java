package be.ugent.zeus.hydra.data.database.minerva2.migrations;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.util.Log;

/**
 * An older migration, kept for compatibility reasons.
 *
 * @author Niko Strijbol
 */
public class Migration_7_8 extends Migration {

    public Migration_7_8() {
        super(7, 8);
    }

    @Override
    public void migrate(SupportSQLiteDatabase supportSQLiteDatabase) {

        Log.i("Migrations", "Migrating database from " + this.startVersion + " to " + this.endVersion);

        // Add the column
        supportSQLiteDatabase.execSQL("ALTER TABLE minerva_courses ADD COLUMN ordering INTEGER NOT NULL DEFAULT 0");
    }
}