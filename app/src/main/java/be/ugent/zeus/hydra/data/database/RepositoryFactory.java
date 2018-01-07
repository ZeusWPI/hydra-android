package be.ugent.zeus.hydra.data.database;

import android.content.Context;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.data.database.feed.CardDatabaseRepository;
import be.ugent.zeus.hydra.data.database.minerva.AgendaDatabaseRepository;
import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDatabaseRepository;
import be.ugent.zeus.hydra.data.database.minerva.CourseDatabaseRepository;
import be.ugent.zeus.hydra.data.dto.minerva.AgendaMapper;
import be.ugent.zeus.hydra.data.dto.minerva.AnnouncementMapper;
import be.ugent.zeus.hydra.data.dto.minerva.CourseMapper;
import be.ugent.zeus.hydra.domain.repository.AgendaItemRepository;
import be.ugent.zeus.hydra.domain.repository.AnnouncementRepository;
import be.ugent.zeus.hydra.domain.repository.CardRepository;
import be.ugent.zeus.hydra.domain.repository.CourseRepository;

/**
 * Provides implementations of the repository interfaces.
 *
 * @author Niko Strijbol
 */
public class RepositoryFactory {

    private static AgendaItemRepository agendaItemRepository;
    private static AnnouncementRepository announcementRepository;
    private static CourseRepository courseRepository;
    private static CardRepository cardRepository;

    @NonNull
    public static synchronized AgendaItemRepository getAgendaItemRepository(Context context) {
        if (agendaItemRepository == null) {
            Database database = Database.get(context);
            agendaItemRepository = new AgendaDatabaseRepository(database.getAgendaDao(), CourseMapper.INSTANCE, AgendaMapper.INSTANCE);
        }
        return agendaItemRepository;
    }

    @NonNull
    public static synchronized AnnouncementRepository getAnnouncementRepository(Context context) {
        if (announcementRepository == null) {
            Database database = Database.get(context);
            announcementRepository = new AnnouncementDatabaseRepository(database.getAnnouncementDao(), CourseMapper.INSTANCE, AnnouncementMapper.INSTANCE);
        }
        return announcementRepository;
    }

    @NonNull
    public static synchronized CourseRepository getCourseRepository(Context context) {
        if (courseRepository == null) {
            Database database = Database.get(context);
            courseRepository = new CourseDatabaseRepository(database.getCourseDao(), CourseMapper.INSTANCE);
        }
        return courseRepository;
    }

    @NonNull
    public static synchronized CardRepository getCardRepository(Context context) {
        if (cardRepository == null) {
            Database database = Database.get(context);
            cardRepository = new CardDatabaseRepository(database.getCardDao());
        }
        return cardRepository;
    }
}