package be.ugent.zeus.hydra.minerva.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * An abstract doa.
 *
 * On initialisation, this class constructs an instance of a {@link android.database.sqlite.SQLiteOpenHelper}.
 *
 * By design, you do NOT need to close database connections in android, as long as you use only one. Every dao
 * uses the same helper instance, so this should not be a problem.
 *
 * @author Niko Strijbol
 */
public abstract class Dao<E> {

    protected final SQLiteOpenHelper helper;
    protected final Context context;
    protected final DatabaseBroadcaster broadcaster;

    /**
     * @param context The application context.
     */
    public Dao(Context context) {
        this.context = context.getApplicationContext();
        this.helper = DatabaseHelper.getInstance(context);
        this.broadcaster = new DatabaseBroadcaster(context);
    }
}