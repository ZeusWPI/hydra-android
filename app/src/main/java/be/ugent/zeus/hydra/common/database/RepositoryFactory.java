package be.ugent.zeus.hydra.common.database;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.feed.cards.CardRepository;
import be.ugent.zeus.hydra.feed.cards.database.CardDatabaseRepository;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementDatabaseRepository;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementMapper;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementRepository;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItemRepository;
import be.ugent.zeus.hydra.minerva.calendar.database.AgendaDatabaseRepository;
import be.ugent.zeus.hydra.minerva.calendar.database.AgendaMapper;
import be.ugent.zeus.hydra.minerva.course.CourseRepository;
import be.ugent.zeus.hydra.minerva.course.database.CourseDatabaseRepository;
import be.ugent.zeus.hydra.minerva.course.database.CourseMapper;

/**
 * Provides implementations of the repository interfaces.
 *
 * @author Niko Strijbol
 */
public class RepositoryFactory {

    @NonNull
    public static synchronized AgendaItemRepository getAgendaItemRepository(Context context) {
        Database database = Database.get(context);
        return new AgendaDatabaseRepository(database.getAgendaDao(), CourseMapper.INSTANCE, AgendaMapper.INSTANCE);
    }

    @NonNull
    public static synchronized AnnouncementRepository getAnnouncementRepository(Context context) {
        Database database = Database.get(context);
        return new AnnouncementDatabaseRepository(database.getAnnouncementDao(), CourseMapper.INSTANCE, AnnouncementMapper.INSTANCE);
    }

    @NonNull
    public static synchronized CourseRepository getCourseRepository(Context context) {
        Database database = Database.get(context);
        return new CourseDatabaseRepository(database.getCourseDao(), CourseMapper.INSTANCE);
    }

    @NonNull
    public static synchronized CardRepository getCardRepository(Context context) {
        Database database = Database.get(context);
        return new CardDatabaseRepository(database.getCardDao());
    }
}