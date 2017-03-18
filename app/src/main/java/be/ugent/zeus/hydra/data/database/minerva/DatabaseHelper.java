package be.ugent.zeus.hydra.data.database.minerva;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.preference.PreferenceManager;
import be.ugent.zeus.hydra.ui.preferences.MinervaFragment;
import be.ugent.zeus.hydra.data.auth.AccountUtils;
import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.data.sync.course.Adapter;
import be.ugent.zeus.hydra.data.sync.SyncUtils;

/**
 * The database helper.
 *
 * @author Niko Strijbol
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String NAME = "minervaDatabase.db";
    private static final int VERSION = 7;

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
        if (oldVersion < 7) {
            upgradeFrom6to7(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= 7) {
            // Manually use table name, since the code might not be available.
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

        /* ----------------------------------------------------------------------
         * Schedule new sync adapters, if the current sync adapter is set.
         * ---------------------------------------------------------------------- */
        // Read current settings.

        if (!AccountUtils.hasAccount(context)) {
            return; // There is no account for some reason.
        }

        Account account = AccountUtils.getAccount(context);

        // The current adapter is set, we set the new ones, otherwise not.
        if (ContentResolver.getSyncAutomatically(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY)) {
            // Set the agenda with the same frequency as the announcements.
            long announcementFrequency = SyncUtils.frequencyFor(context, MinervaConfig.ANNOUNCEMENT_AUTHORITY);
            SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
            p.edit()
                    .putString(MinervaFragment.PREF_SYNC_FREQUENCY_CALENDAR, String.valueOf(announcementFrequency))
                    .apply();

            // Request a full synchronisation
            Bundle bundle = new Bundle();
            bundle.putBoolean(Adapter.EXTRA_SCHEDULE_ANNOUNCEMENTS, true);
            bundle.putBoolean(Adapter.EXTRA_SCHEDULE_AGENDA, true);
            SyncUtils.requestSync(account, MinervaConfig.COURSE_AUTHORITY, bundle);
        }
    }
}