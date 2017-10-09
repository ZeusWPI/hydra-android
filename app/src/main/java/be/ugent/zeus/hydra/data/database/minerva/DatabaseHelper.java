package be.ugent.zeus.hydra.data.database.minerva;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The database helper.
 *
 * @author Niko Strijbol
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String NAME = "minervaDatabase.db";
    private static final int VERSION = 9;

    //Singleton - can we avoid this? Should we? I don't know.
    private static DatabaseHelper instance;

    private final Context context;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), NAME, null, VERSION);
        this.context = context.getApplicationContext();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Make the table with the courses.
        db.execSQL(CourseTable.createTableQuery());
        //Make the table with the announcements
        db.execSQL(AnnouncementTable.createTableQuery());
        //Make the table with the agenda
        db.execSQL(AgendaTable.createTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion != newVersion) {
            Log.i(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
        }

        if (oldVersion < 7) {
            upgradeFrom6to7(db);
        }
        if (oldVersion < 8) {
            upgradeFrom7to8(db);
        }
        if (oldVersion < 9) {
            upgradeFrom8to9(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Manually use table name, since the code might not be available.
        if (oldVersion >= 8) {
            db.execSQL("ALTER TABLE " + CourseTable.TABLE_NAME + " DROP COLUMN ordering");
        }
        if (oldVersion >= 7) {
            db.execSQL("ALTER TABLE " + AgendaTable.TABLE_NAME + " DROP COLUMN calendar_id");
        }
    }

    /**
     * Upgrade the database from version 6 to version 7.
     *
     * @param db The database to upgrade.
     */
    private void upgradeFrom6to7(SQLiteDatabase db) {
        /* -----------------------------------------
         * Upgrade the database structure
         * ----------------------------------------- */
        // Add column
        db.execSQL("ALTER TABLE " + AgendaTable.TABLE_NAME + " ADD COLUMN " + AgendaTable.Columns.CALENDAR_ID + " INTEGER");
        // Add data
        db.execSQL("UPDATE " + AgendaTable.TABLE_NAME + " SET " + AgendaTable.Columns.CALENDAR_ID + "=-1");
    }

    private void upgradeFrom7to8(SQLiteDatabase db) {
        // Add the column
        db.execSQL("ALTER TABLE " + CourseTable.TABLE_NAME + " ADD COLUMN " + CourseTable.Columns.ORDER + " INTEGER NOT NULL DEFAULT 0");
    }

    private void upgradeFrom8to9(SQLiteDatabase db) {
        // Add the column
        db.execSQL("ALTER TABLE minerva_agenda ADD COLUMN is_merged INTEGER NOT NULL DEFAULT 0");
    }
}