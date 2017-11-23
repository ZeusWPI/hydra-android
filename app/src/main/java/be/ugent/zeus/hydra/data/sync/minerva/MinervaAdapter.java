package be.ugent.zeus.hydra.data.sync.minerva;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.data.database.minerva2.RepositoryFactory;
import be.ugent.zeus.hydra.data.sync.AbstractAdapter;
import be.ugent.zeus.hydra.data.sync.SyncUtils;
import be.ugent.zeus.hydra.data.sync.minerva.helpers.AnnouncementSync;
import be.ugent.zeus.hydra.data.sync.minerva.helpers.CalendarSync;
import be.ugent.zeus.hydra.data.sync.minerva.helpers.CourseSync;
import be.ugent.zeus.hydra.domain.repository.AgendaItemRepository;
import be.ugent.zeus.hydra.domain.repository.AnnouncementRepository;
import be.ugent.zeus.hydra.domain.repository.CourseRepository;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import jonathanfinerty.once.Once;

/**
 * The sync adapter for Minerva announcements.
 *
 * When requesting a sync, the requester can choose which components are synchronised and not, by using the
 * SYNC_* flags. When set to true, the component will be synchronised, when set to false it won't. By default,
 * all components will be synchronised.
 *
 * The adapter uses various indicators to determine if this is the first synchronisation or not. Firstly, when
 * requesting a synchronisation just after an account has been added, set the {@link #EXTRA_FIRST_SYNC} flag to true.
 * Secondly, to detect when the user has cleared the app data, which does not clear the account, the adapter keeps
 * track of the first sync using {@link Once}.
 *
 * In addition to the above, the adapter keeps track if a first sync with the built-in calendar has happend separately.
 * Because on later editions of Android the first sync may be run while we don't have permission yet. After permission
 * has been granted, the sync will run again, but only for the calendar (and courses), which is then the first sync
 * if we only consider the built-in calendar. This is done because clearing the app data does not clear the calendar,
 * meaning duplicates would be present on later synchronisations.
 *
 * @author Niko Strijbol
 */
public class MinervaAdapter extends AbstractAdapter {

    /**
     * This is a boolean flag; and is false by default.
     *
     * Indicate that this is the first synchronisation for an account. This will prompt a removal of any present data,
     * since Android sometimes deletes accounts without removing data.
     *
     * It will also suppress notifications about newly synchronised items, regardless of the user settings. When syncing
     * for the first time, the user does not want to be bombarded with notifications about new announcements.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String EXTRA_FIRST_SYNC = "firstSync";

    /**
     * Set this value to false in the extras to disable syncing of the courses.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String SYNC_COURSES = "be.ugent.zeus.hydra.sync.minerva.courses";
    /**
     * Set this value to false in the extras to disable syncing of the announcements.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String SYNC_ANNOUNCEMENTS = "be.ugent.zeus.hydra.sync.minerva.announcements";
    /**
     * Set this value to false in the extras to disable syncing of the calendar.
     */
    @SuppressWarnings("WeakerAccess")
    public static final String SYNC_CALENDAR = "be.ugent.zeus.hydra.sync.minerva.calendar";

    private static final String TAG = "MinervaAdapter";
    private static final String FIRST_SYNC = "once_first_sync";

    MinervaAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    protected void onPerformCheckedSync(Account account,
                                        Bundle extras,
                                        String authority,
                                        ContentProviderClient provider,
                                        SyncResult results) throws RequestException {

        // Get the dao's we need.
        CourseRepository courseDao = RepositoryFactory.getCourseRepository(getContext());
        AnnouncementRepository announcementDao = RepositoryFactory.getAnnouncementRepository(getContext());
        AgendaItemRepository agendaDao = RepositoryFactory.getAgendaItemRepository(getContext());

        boolean isInitialSync = extras.getBoolean(EXTRA_FIRST_SYNC, false) || !Once.beenDone(FIRST_SYNC);

        // If it is the first sync, delete everything.
        if (isInitialSync) {
            Log.i(TAG, "Clearing database, as this is the first sync.");
            agendaDao.deleteAll();
            announcementDao.deleteAll();
            courseDao.deleteAll();
        }

        // First, we synchronise the courses.
        if (extras.getBoolean(SYNC_COURSES, true) && !isCancelled) {
            broadcast.publishIntent(SyncBroadcast.SYNC_COURSES);
            CourseSync courseSync = new CourseSync(courseDao, getContext());
            courseSync.synchronise(account);
        }

        // Then we sync the agenda.
        if (extras.getBoolean(SYNC_CALENDAR, true) && !isCancelled) {
            broadcast.publishIntent(SyncBroadcast.SYNC_AGENDA);
            CalendarSync calendarSync = new CalendarSync(agendaDao, courseDao, getContext());
            if (calendarSync.synchronise(account, isInitialSync)) {
                // Request a new sync and stop this one.
                SyncUtils.requestSync(account, MinervaConfig.SYNC_AUTHORITY, new Bundle());
                return;
            }
        }

        if (extras.getBoolean(SYNC_ANNOUNCEMENTS, true) && !isCancelled) {
            // Finally, we sync the announcements.
            AnnouncementSync announcementSync = new AnnouncementSync(getContext(), announcementDao, courseDao, new AnnouncementSync.Companion() {
                @Override
                public boolean isCancelled() {
                    return isCancelled;
                }

                @Override
                public void onProgress(int active, int total) {
                    broadcast.publishAnnouncementDone(active, total);
                }
            });
            announcementSync.synchronise(account, isInitialSync);
        }

        // Enable normal synchronisation.
        if (isInitialSync) {
            Log.i(TAG, "Enabling normal synchronisation.");
            long frequency = SyncUtils.frequencyFor(getContext());
            SyncUtils.enable(account, MinervaConfig.SYNC_AUTHORITY, frequency);

            // The first sync has been completed.
            Once.markDone(FIRST_SYNC);
        }
    }
}