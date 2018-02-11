package be.ugent.zeus.hydra.common.database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import be.ugent.zeus.hydra.feed.cards.database.CardDao;
import be.ugent.zeus.hydra.common.database.migrations.*;
import be.ugent.zeus.hydra.minerva.calendar.database.AgendaDao;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.course.database.CourseDao;
import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import be.ugent.zeus.hydra.minerva.calendar.database.AgendaItemDTO;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementDTO;
import be.ugent.zeus.hydra.minerva.course.database.CourseDTO;
import be.ugent.zeus.hydra.feed.cards.CardDismissal;

import static be.ugent.zeus.hydra.common.database.Database.VERSION;

/**
 * The database for Minerva-related stuff.
 * <p>
 * The database is implemented as a Room database. This class should be a singleton, as it is fairly expensive.
 *
 * @author Niko Strijbol
 */
@android.arch.persistence.room.Database(entities = {
        CourseDTO.class, AgendaItemDTO.class, AnnouncementDTO.class, // Minerva
        CardDismissal.class // Feed stuff
}, version = VERSION)
@TypeConverters(DateTypeConverters.class)
public abstract class Database extends RoomDatabase {

    /**
     * The current version of the database. When changing this value, you must provide a appropriate migration, or the
     * app will crash.
     */
    static final int VERSION = 14;
    /**
     * The current name of the database. Should not change.
     * <p>
     * The name of the database is historically determined, although the database contains more than just
     * Minerva-related things these days.
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
    static synchronized Database get(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, Database.class, NAME)
                    .allowMainThreadQueries() // TODO
                    .addMigrations(new Migration_6_7(), new Migration_7_8(), new Migration_8_9(), new Migration_9_10(),
                            new Migration_10_11(), new Migration_11_12(), new Migration_12_13(), new Migration_13_14()
                    )
                    .build();
        }
        return instance;
    }

    /**
     * Get an implementation of the course dao. This method is safe to call multiple times; only one instance will be
     * created and returned.
     *
     * @return Instance of the course dao.
     */
    public abstract CourseDao getCourseDao();

    /**
     * Get an implementation of the announcement dao. This method is safe to call multiple times; only one instance will
     * be created and returned.
     *
     * @return Instance of the announcement dao.
     */
    public abstract AnnouncementDao getAnnouncementDao();

    /**
     * Get an implementation of the calendar dao. This method is safe to call multiple times; only one instance will be
     * created and returned.
     *
     * @return Instance of the calendar dao.
     */
    public abstract AgendaDao getAgendaDao();

    /**
     * Get an implementation of the card dao. This method is safe to call multiple times; only one instance will be
     * created and returned.
     *
     * @return Instance of the card dao.
     */
    public abstract CardDao getCardDao();
}