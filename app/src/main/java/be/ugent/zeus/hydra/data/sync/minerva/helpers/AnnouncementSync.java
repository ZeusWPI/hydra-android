package be.ugent.zeus.hydra.data.sync.minerva.helpers;

import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDao;
import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.models.minerva.Announcement;
import be.ugent.zeus.hydra.data.models.minerva.Announcements;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.models.minerva.WhatsNew;
import be.ugent.zeus.hydra.data.network.requests.minerva.AnnouncementsRequest;
import be.ugent.zeus.hydra.data.network.requests.minerva.WhatsNewRequest;
import be.ugent.zeus.hydra.data.sync.Synchronisation;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import be.ugent.zeus.hydra.ui.preferences.MinervaFragment;
import java8.util.Maps;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Syncs the announcements for Minerva.
 * <p>
 * The exact details of the synchronisation algorithm can be found by looking at the {@link #synchronise(Account, boolean)} ()} method.
 *
 * @author Niko Strijbol
 */
public class AnnouncementSync {

    /**
     * Provides access to the announcements in the database.
     */
    private final AnnouncementDao announcementDao;

    /**
     * Provides access to the courses in the database.
     */
    private final CourseDao courseDao;

    private final Context context;
    private final SharedPreferences preferences;

    /**
     * Helps constructing and showing notifications.
     */
    private final NotificationHelper notificationHelper;

    /**
     * Communicates the status and enables cancellation.
     */
    private final Companion companion;

    public AnnouncementSync(Context context, AnnouncementDao announcementDao, CourseDao courseDao, Companion companion) {
        this.context = context;
        this.announcementDao = announcementDao;
        this.courseDao = courseDao;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.notificationHelper = new NotificationHelper(context);
        this.companion = companion;
    }

    /**
     * Execute the synchronisation.
     *
     * @param account       The account for which the synchronisation happens.
     * @param isInitialSync If this is the initial sync, e.g. when adding an account.
     *
     * @throws RequestException If something went wrong.
     */
    @WorkerThread
    public void synchronise(@NonNull Account account, boolean isInitialSync) throws RequestException {

        // Get all courses from the database.
        List<Course> courses = courseDao.getAll();

        // Synchronise the announcements for each course.
        for (int i = 0; i < courses.size(); i++) {
            if (companion.isCancelled()) {
                break;
            }
            companion.onProgress(i + 1, courses.size());
            synchroniseCourse(account, courses.get(i), isInitialSync);
        }
    }

    /**
     * Synchronise the announcements for one course.
     *
     * This method will also take care of managing notifications.
     *
     * @param account The account.
     * @param course  The course to synchronise.
     *
     * @throws RequestException If something goes wrong contacting the server.
     */
    private void synchroniseCourse(Account account, Course course, boolean isInitialSync) throws RequestException {

        // Get all announcements from the server.
        AnnouncementsRequest request = new AnnouncementsRequest(context, account, course);
        Announcements serverAnnouncements = request.performRequest(null).getOrThrow();

        // Get the announcements from the database. This returns the ID of the announcements and the read
        // date, since we want to preserve that.
        Map<Integer, ZonedDateTime> existing = announcementDao.getIdsAndReadDateForCourse(course);

        // We calculate the diff.
        Synchronisation<Announcement, Integer> synchronisation = new Synchronisation<>(
                existing.keySet(),
                serverAnnouncements.getAnnouncements(),
                Announcement::getItemId
        );
        Synchronisation.Diff<Announcement, Integer> diff = synchronisation.diff();

        // Get which requests are unread on the server.
        WhatsNewRequest whatsNewRequest = new WhatsNewRequest(course, context, account);
        WhatsNew whatsNew = whatsNewRequest.performRequest(null).getOrThrow();
        List<Announcement> unreadOnServer = new ArrayList<>(whatsNew.getAnnouncements());

        // Check if we need to notify for announcements that have been sent as an e-mail.
        boolean notifyIfEmailWasSent = preferences.getBoolean(
                MinervaFragment.PREF_ANNOUNCEMENT_NOTIFICATION_EMAIL,
                MinervaFragment.PREF_DEFAULT_ANNOUNCEMENT_NOTIFICATION_EMAIL
        );
        boolean showNotifications = preferences.getBoolean(MinervaFragment.PREF_ANNOUNCEMENT_NOTIFICATION, true);

        ZonedDateTime now = ZonedDateTime.now();

        // Synchronise the read date for updated announcements (thus those that are already in the database).
        for (Announcement announcement : diff.getUpdated()) {
            announcement.setCourse(course);
            if (unreadOnServer.contains(announcement)) {
                // If the announcement is unread on the server, we get the local read date. If this date was not null,
                // the user has read the notification locally, but not yet online, so we don't mark it as unread.
                announcement.setRead(existing.get(announcement.getItemId()));
            } else {
                // This announcement was read on the server, so mark is as read here as well.
                // We try to get it from the map, so we don't overwrite the local date.
                announcement.setRead(Maps.getOrDefault(existing, announcement.getItemId(), now));
            }
        }

        // Contains announcements for which we want to show a notification.
        List<Announcement> toNotify = new ArrayList<>();

        // Check what to do with new announcements.
        for (Announcement announcement : diff.getNew()) {
            announcement.setCourse(course);
            if (unreadOnServer.contains(announcement)) {
                // The new announcement is also unread on the server.
                if (!isInitialSync && showNotifications && (notifyIfEmailWasSent || !announcement.isEmailSent())) {
                    // Check if we want to show announcements or not.
                    toNotify.add(announcement);
                }
            } else {
                // The new announcement is already marked as read on the server.
                announcement.setRead(now);
            }
        }

        // Do the synchronisation.
        diff.apply(announcementDao);

        // Firstly, we delete any notifications for announcements that are not here anymore.
        notificationHelper.cancel(course, diff.getStaleIds());

        // The we make the new announcements, if there are any.
        notificationHelper.showNotificationsFor(course, toNotify);
    }

    /**
     * Enables communications with the progress here.
     */
    public interface Companion {
        boolean isCancelled();
        void onProgress(int active, int total);
    }
}