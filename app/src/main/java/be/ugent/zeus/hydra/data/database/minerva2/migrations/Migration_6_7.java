package be.ugent.zeus.hydra.data.database.minerva2.migrations;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.util.Log;

import be.ugent.zeus.hydra.data.database.minerva2.agenda.AgendaTable;

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
    public void migrate(SupportSQLiteDatabase supportSQLiteDatabase) {

        Log.i("Migrations", "Migrating database from " + this.startVersion + " to " + this.endVersion);

        /* -----------------------------------------
         * Upgrade the database structure
         * ----------------------------------------- */
        // Add column
        supportSQLiteDatabase.execSQL("ALTER TABLE " + AgendaTable.TABLE_NAME + " ADD COLUMN " + AgendaTable.Columns.CALENDAR_ID + " INTEGER");
        // Add data
        supportSQLiteDatabase.execSQL("UPDATE " + AgendaTable.TABLE_NAME + " SET " + AgendaTable.Columns.CALENDAR_ID + "=-1");
    }
}
