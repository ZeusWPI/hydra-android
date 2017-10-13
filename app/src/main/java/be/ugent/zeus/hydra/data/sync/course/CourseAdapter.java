package be.ugent.zeus.hydra.data.sync.course;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import be.ugent.zeus.hydra.data.ChannelCreator;
import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.data.database.minerva.CourseDao;
import be.ugent.zeus.hydra.data.models.minerva.Course;
import be.ugent.zeus.hydra.data.models.minerva.Courses;
import be.ugent.zeus.hydra.repository.requests.RequestException;
import be.ugent.zeus.hydra.data.network.requests.minerva.CoursesMinervaRequest;
import be.ugent.zeus.hydra.data.sync.MinervaAdapter;
import be.ugent.zeus.hydra.data.sync.SyncBroadcast;
import be.ugent.zeus.hydra.data.sync.SyncUtils;
import be.ugent.zeus.hydra.data.sync.Synchronisation;

import java.util.Collection;

/**
 * Sync adapter for the list of Minerva courses.
 *
 * Implementing this as a separate sync adapters has both advantages and disadvantages. The main advantage is that we
 * don't have to execute this as often as the other adapters.
 *
 * The main disadvantage is that we may have an old list of courses, so we need support for this in the other sync
 * adapters. When they encounter a course they don't know, they need to abort and schedule this sync adapter. This is
 * of course less efficient than directly syncing both.
 *
 * We think the advantage outweighs the disadvantage: normally, the courses of a student only change twice a year: at
 * the start of both semesters.
 *
 * To facilitate dealing with the disadvantage, two flags are provided: {@link #EXTRA_SCHEDULE_AGENDA} and
 * {@link #EXTRA_SCHEDULE_ANNOUNCEMENTS}. When settings these flags to true, this adapter will schedule a synchronisation
 * of the calendar and/or announcements. The {@link #EXTRA_FIRST_SYNC} flag will be preserved.
 *
 * @author Niko Strijbol
 */
public class CourseAdapter extends MinervaAdapter {

    private static final String TAG = "MinervaCalendarAdapter";

    /**
     * Using this flag will cause the sync adapter to schedule a synchronisation of the announcements immediately after
     * the course synchronisation finishes. If both this flag and {@link #EXTRA_SCHEDULE_AGENDA} are used, both will
     * be started at the same time.
     */
    public static final String EXTRA_SCHEDULE_ANNOUNCEMENTS = "scheduleAnnouncements";

    /**
     * Using this flag will cause the sync adapter to schedule a synchronisation of the calendar immediately after the
     * course synchronisation finishes. If both this flag and {@link #EXTRA_SCHEDULE_ANNOUNCEMENTS} are used, both will
     * be started at the same time.
     */
    public static final String EXTRA_SCHEDULE_AGENDA = "scheduleAgenda";

    public CourseAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    protected void onPerformCheckedSync(Account account,
                                        Bundle extras,
                                        String authority,
                                        ContentProviderClient provider,
                                        SyncResult results,
                                        boolean isFirstSync) throws RequestException {

        // Make sure the notification channel is present
        ChannelCreator channelCreator = ChannelCreator.getInstance(getContext());
        channelCreator.createMinervaAccountChannel();

        final CourseDao courseDao = new CourseDao(getContext());
        final CoursesMinervaRequest minervaRequest = new CoursesMinervaRequest(getContext(), account);

        // Calculate diff
        Courses courses = minervaRequest.performRequest(null).getOrThrow();
        Collection<String> existingIds = courseDao.getIds();
        Synchronisation<Course, String> synchronisation = new Synchronisation<>(
                existingIds,
                courses.getCourses(),
                Course::getId
        );
        // Execute diff
        Synchronisation.Diff<Course, String> diff = synchronisation.diff();
        diff.apply(courseDao);

        // Publish progress.
        broadcast.publishIntent(SyncBroadcast.SYNC_COURSES);
    }

    @Override
    protected void afterSync(Account account, Bundle extras, boolean isFirstSync) {

        if (isFirstSync) {
            long frequency = SyncUtils.frequencyFor(getContext(), MinervaConfig.COURSE_AUTHORITY);
            SyncUtils.enable(account, MinervaConfig.COURSE_AUTHORITY, frequency);
        }

        // Schedule other synchronisations if needed.
        if (extras.getBoolean(EXTRA_SCHEDULE_AGENDA, false)) {
            Log.d(TAG, "Requesting calendar sync...");
            Bundle bundle = new Bundle();
            bundle.putBoolean(EXTRA_FIRST_SYNC, isFirstSync);
            SyncUtils.requestSync(account, MinervaConfig.CALENDAR_AUTHORITY, bundle);
        }
        if (extras.getBoolean(EXTRA_SCHEDULE_ANNOUNCEMENTS, false)) {
            Log.d(TAG, "Requesting announcement sync...");
            Bundle bundle = new Bundle();
            bundle.putBoolean(EXTRA_FIRST_SYNC, isFirstSync);
            SyncUtils.requestSync(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY, bundle);
        }
    }
}