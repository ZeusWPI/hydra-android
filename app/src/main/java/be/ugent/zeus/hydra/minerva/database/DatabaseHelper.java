package be.ugent.zeus.hydra.minerva.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import be.ugent.zeus.hydra.minerva.agenda.AgendaTable;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementTable;
import be.ugent.zeus.hydra.minerva.course.CourseTable;

/**
 * The database helper.
 *
 * @author Niko Strijbol
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String NAME = "minervaDatabase.db";
    private static final int VERSION = 6;

    //Singleton - can we avoid this? Should we? I don't know.
    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHelper(context);
        }

        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context.getApplicationContext(), NAME, null, VERSION);
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
        if(oldVersion < newVersion) {
            //Do database upgrade.
            Log.i(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
            db.execSQL(AgendaTable.dropIfExistQuery());
            db.execSQL(AnnouncementTable.dropIfExistQuery());
            db.execSQL(CourseTable.dropIfExistQuery());
            onCreate(db);
        }
    }
}