package be.ugent.zeus.hydra.common.database;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import be.ugent.zeus.hydra.common.database.migrations.*;
import be.ugent.zeus.hydra.feed.cards.CardDismissal;
import be.ugent.zeus.hydra.feed.cards.database.CardDao;

import static be.ugent.zeus.hydra.common.database.Database.VERSION;

/**
 * The database for Hydra.
 *
 * The database is implemented as a Room database. This class should be a singleton, as it is fairly expensive.
 *
 * @author Niko Strijbol
 */
@androidx.room.Database(entities = {
        CardDismissal.class // Feed stuff
}, version = VERSION)
@TypeConverters(DateTypeConverters.class)
public abstract class Database extends RoomDatabase {

    private static final Object LOCK = new Object();

    /**
     * The current version of the database. When changing this value, you must provide a appropriate migration, or the
     * app will crash.
     */
    static final int VERSION = 15;
    /**
     * The current name of the database. Should not change.
     *
     * The name of the database is historically determined, although the database no longer contains any Minerva-related
     * things.
     */
    private static final String NAME = "minervaDatabase.db";
    private static Database instance;

    /**
     * Create a new instance of the database.
     *
     * @param context The context. Must be suitable for {@link Room#databaseBuilder(Context, Class, String)}.
     *
     * @return An instance of the database.
     */
    public static Database get(Context context) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, NAME)
                        .allowMainThreadQueries() // TODO
                        .addMigrations(new Migration_6_7(), new Migration_7_8(), new Migration_8_9(), new Migration_9_10(),
                                new Migration_10_11(), new Migration_11_12(), new Migration_12_13(), new Migration_13_14(),
                                new Migration_14_15()
                        )
                        .build();
            }
        }
        return instance;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public static void reset() {
        synchronized (LOCK) {
            instance = null;
        }
    }

    /**
     * Get an implementation of the card dao. This method is safe to call multiple times; only one instance will be
     * created and returned.
     *
     * @return Instance of the card dao.
     */
    public abstract CardDao getCardDao();
}