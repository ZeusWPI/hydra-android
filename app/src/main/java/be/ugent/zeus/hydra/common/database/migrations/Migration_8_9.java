package be.ugent.zeus.hydra.common.database.migrations;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.migration.Migration;
import androidx.annotation.NonNull;
import android.util.Log;

/**
 * This is a legacy migration.
 *
 * @author Niko Strijbol
 */
public class Migration_8_9 extends Migration {

    public Migration_8_9() {
        super(8, 9);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {

        Log.i("Migrations", "Migrating database from " + this.startVersion + " to " + this.endVersion);

        // Add the column
        supportSQLiteDatabase.execSQL("ALTER TABLE minerva_agenda ADD COLUMN is_merged INTEGER NOT NULL DEFAULT 0");
    }
}