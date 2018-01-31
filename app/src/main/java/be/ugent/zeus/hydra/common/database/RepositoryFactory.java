package be.ugent.zeus.hydra.common.database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.feed.CardRepository;
import be.ugent.zeus.hydra.feed.database.CardDatabaseRepository;
import be.ugent.zeus.hydra.minerva.course.CourseRepository;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementDatabaseRepository;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementRepository;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItemRepository;
import be.ugent.zeus.hydra.minerva.calendar.database.AgendaDatabaseRepository;
import be.ugent.zeus.hydra.minerva.course.database.CourseDatabaseRepository;
import be.ugent.zeus.hydra.minerva.calendar.database.AgendaMapper;
import be.ugent.zeus.hydra.minerva.announcement.database.AnnouncementMapper;
import be.ugent.zeus.hydra.minerva.course.database.CourseMapper;

import java.lang.ref.WeakReference;

/**
 * Provides implementations of the repository interfaces.
 *
 * @author Niko Strijbol
 */
public class RepositoryFactory {

    private static final String TAG = "RepositoryFactory";

    private static WeakReference<AgendaItemRepository> calendar = new WeakReference<>(null);
    private static WeakReference<AnnouncementRepository> announcements = new WeakReference<>(null);
    private static WeakReference<CourseRepository> courses = new WeakReference<>(null);
    private static WeakReference<CardRepository> cards = new WeakReference<>(null);

    @NonNull
    public static synchronized AgendaItemRepository getAgendaItemRepository(Context context) {
        AgendaItemRepository repository = calendar.get();
        if (repository == null) {
            Log.d(TAG, "getAgendaItemRepository: create calendar repository");
            Database database = Database.get(context);
            repository = new AgendaDatabaseRepository(database.getAgendaDao(), CourseMapper.INSTANCE, AgendaMapper.INSTANCE);
            calendar = new WeakReference<>(repository);
        }
        return repository;
    }

    @NonNull
    public static synchronized AnnouncementRepository getAnnouncementRepository(Context context) {
        AnnouncementRepository repository = announcements.get();
        if (repository == null) {
            Log.d(TAG, "getAnnouncementRepository: creating announcement repo");
            Database database = Database.get(context);
            repository = new AnnouncementDatabaseRepository(database.getAnnouncementDao(), CourseMapper.INSTANCE, AnnouncementMapper.INSTANCE);
            announcements = new WeakReference<>(repository);
        }
        return repository;
    }

    @NonNull
    public static synchronized CourseRepository getCourseRepository(Context context) {
        CourseRepository repository = courses.get();
        if (repository == null) {
            Log.d(TAG, "getCourseRepository: creating course repo");
            Database database = Database.get(context);
            repository = new CourseDatabaseRepository(database.getCourseDao(), CourseMapper.INSTANCE);
            courses = new WeakReference<>(repository);
        }
        return repository;
    }

    @NonNull
    public static synchronized CardRepository getCardRepository(Context context) {
        CardRepository repository = cards.get();
        if (repository == null) {
            Log.d(TAG, "getCardRepository: creating card repository");
            Database database = Database.get(context);
            repository = new CardDatabaseRepository(database.getCardDao());
            cards = new WeakReference<>(repository);
        }
        return repository;
    }
}