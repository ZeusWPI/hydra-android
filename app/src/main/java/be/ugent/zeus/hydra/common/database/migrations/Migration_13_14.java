package be.ugent.zeus.hydra.common.database.migrations;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * @author Niko Strijbol
 */
public class Migration_13_14 extends Migration {

    private static final String TAG = "Migration_13_14";

    public Migration_13_14() {
        super(13, 14);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        Log.i(TAG, "Migrating database from " + this.startVersion + " to " + this.endVersion);

        database.execSQL("ALTER TABLE minerva_courses ADD COLUMN ignore_announcements INTEGER NOT NULL DEFAULT 0");
        database.execSQL("ALTER TABLE minerva_courses ADD COLUMN ignore_calendar INTEGER NOT NULL DEFAULT 0");

        Log.i(TAG, "Migration completed.");
    }
}