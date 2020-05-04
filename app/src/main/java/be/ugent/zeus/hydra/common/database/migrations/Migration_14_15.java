package be.ugent.zeus.hydra.common.database.migrations;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Drop all Minerva-related tables, since Minerva no longer exists.
 *
 * @author Niko Strijbol
 */
public class Migration_14_15 extends Migration {

    private static final String TAG = "Migration_14_15";

    public Migration_14_15() {
        super(14, 15);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        Log.i(TAG, "Migrating database from " + this.startVersion + " to " + this.endVersion);

        database.execSQL("DROP TABLE minerva_calendar");
        database.execSQL("DROP TABLE minerva_announcements");
        database.execSQL("DROP TABLE minerva_courses");


        Log.i(TAG, "Migration completed.");
    }
}