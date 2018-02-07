package be.ugent.zeus.hydra.data.database.migrations;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * @author Niko Strijbol
 */
public class Migration_12_13 extends Migration {

    private static final String TAG = "Migration_12_13";

    public Migration_12_13() {
        super(12, 13);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        Log.i(TAG, "Migrating database from " + this.startVersion + " to " + this.endVersion);

        database.execSQL("ALTER TABLE minerva_courses ADD COLUMN disabled_modules INTEGER NOT NULL DEFAULT 0");

        Log.i(TAG, "Migration completed.");
    }
}