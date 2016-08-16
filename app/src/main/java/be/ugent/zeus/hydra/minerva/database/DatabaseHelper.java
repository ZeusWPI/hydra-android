package be.ugent.zeus.hydra.minerva.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Niko Strijbol
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    public static final String DATABASE_NAME = "hydra.db";
    public static final int DATABASE_VERSION = 1;

    //Singleton - can we avoid this? Should we? I don't know.
    private static DatabaseHelper instance;


    public static DatabaseHelper getInstance(Context context) {
        if(instance == null) {
            instance = new DatabaseHelper(context);
        }

        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Make the table with the courses.
        db.execSQL(CourseTable.createTableQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < newVersion) {
            //Do database upgrade.
            Log.i(TAG, "Upgrading database from " + oldVersion + " to " + newVersion);
        }
    }
}