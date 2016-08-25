package be.ugent.zeus.hydra.minerva.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * An abstract doa.
 *
 * On initialisation, this class constructs an instance of a {@link android.database.sqlite.SQLiteOpenHelper}.
 *
 * Every method in this class reserves the right to close the database. You need to make sure to get a new database
 * instance after calling a method in this class.
 *
 * @author Niko Strijbol
 */
public abstract class Dao {

    protected final SQLiteOpenHelper helper;

    /**
     * @param context The application context.
     */
    public Dao(Context context) {
        this.helper = DatabaseHelper.getInstance(context);
    }
}
