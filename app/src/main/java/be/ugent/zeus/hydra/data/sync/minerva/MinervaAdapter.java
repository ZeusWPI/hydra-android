package be.ugent.zeus.hydra.data.sync.minerva;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.data.database.minerva.AgendaDao;
import be.ugent.zeus.hydra.data.database.minerva.AnnouncementDao;
import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.sync.AbstractAdapter;
import be.ugent.zeus.hydra.data.sync.SyncUtils;
import be.ugent.zeus.hydra.data.sync.minerva.helpers.CalendarSync;
import be.ugent.zeus.hydra.data.sync.minerva.helpers.CourseSync;
import be.ugent.zeus.hydra.data.sync.minerva.helpers.AnnouncementSync;
import be.ugent.zeus.hydra.repository.requests.RequestException;

/**
 * The sync adapter for Minerva announcements.
 *
 * The sync adapter will broadcast it's progress, so you can subscribe to be updated. See {@link SyncBroadcast}.
 *
 * @author Niko Strijbol
 */
public class MinervaAdapter extends AbstractAdapter {

    /**
     * Set this value to false in the extras to disable syncing of the courses.
     */
    public static final String SYNC_COURSES = "be.ugent.zeus.hydra.sync.minerva.courses";
    /**
     * Set this value to false in the extras to disable syncing of the announcements.
     */
    public static final String SYNC_ANNOUNCEMENTS = "be.ugent.zeus.hydra.sync.minerva.announcements";
    /**
     * Set this value to false in the extras to disable syncing of the calendar.
     */
    public static final String SYNC_CALENDAR = "be.ugent.zeus.hydra.sync.minerva.calendar";
    /**
     * Set this to true to clear the values at the start.
     */
    public static final String SYNC_EMPTY_FIRST  = "be.ugent.zeus.hydra.sync.minerva.restart";

    MinervaAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    protected void onPerformCheckedSync(Account account,
                                        Bundle extras,
                                        String authority,
                                        ContentProviderClient provider,
                                        SyncResult results,
                                        boolean isInitialSync) throws RequestException {

        // Get the dao's we need.
        CourseDao courseDao = new CourseDao(getContext());
        AnnouncementDao announcementDao = new AnnouncementDao(getContext());
        AgendaDao agendaDao = new AgendaDao(getContext());

        // If it is the first sync, delete everything.
        if (isInitialSync || extras.getBoolean(SYNC_EMPTY_FIRST, false)) {
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
    }
}