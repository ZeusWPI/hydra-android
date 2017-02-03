package be.ugent.zeus.hydra.minerva.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.*;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.zeus.hydra.fragments.preferences.MinervaFragment;
import be.ugent.zeus.hydra.minerva.agenda.AgendaDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementDao;
import be.ugent.zeus.hydra.minerva.announcement.AnnouncementNotificationBuilder;
import be.ugent.zeus.hydra.minerva.announcement.SyncObject;
import be.ugent.zeus.hydra.minerva.auth.AuthenticatorActionException;
import be.ugent.zeus.hydra.minerva.course.CourseDao;
import be.ugent.zeus.hydra.minerva.requests.AgendaRequest;
import be.ugent.zeus.hydra.minerva.requests.AnnouncementsRequest;
import be.ugent.zeus.hydra.minerva.requests.CoursesMinervaRequest;
import be.ugent.zeus.hydra.minerva.requests.WhatsNewRequest;
import be.ugent.zeus.hydra.models.minerva.*;
import be.ugent.zeus.hydra.requests.exceptions.IOFailureException;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.threeten.bp.*;

import java.util.Collection;

/**
 * The sync adapter for the Minerva integration. Since Minerva synchronisation is currently one way only, this adapter
 * could be seen as a glorified download manager.
 *
 * There is currently one flags that can be used to influence behavior; {@link #EXTRA_FIRST_SYNC}.
 * See their documentation for details on what they do.
 *
 * The sync adapter will broadcast it's progress, so you can subscribe to be updated. See {@link SyncBroadcast}.
 *
 * @author Niko Strijbol
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    /**
     * This is a boolean flag; and is false by default.
     *
     * Indicate that this is the first synchronisation for an account. This will prompt a removal of any present data,
     * since Android sometimes deletes accounts without removing data.
     *
     * It will also suppress notifications about newly synchronised items, regardless of the user settings. When syncing
     * for the first time, the user does not want to be bombarded with notifications about new announcements.
     */
    public static final String EXTRA_FIRST_SYNC = "firstSync";
    private static final String TAG = "SyncAdapter";
    private final SyncBroadcast broadcast;
    private boolean isCancelled = false;

    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        broadcast = new SyncBroadcast(getContext());
    }

    // TODO write a good, more formal overview of the sync algorithm.
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(TAG, "Starting sync...");
        broadcast.publishIntent(SyncBroadcast.SYNC_START);

        if (isCancelled) {
            broadcast.publishIntent(SyncBroadcast.SYNC_CANCELLED);
            return;
        }

        final boolean isFirstSync = extras.getBoolean(EXTRA_FIRST_SYNC, false);

        //Get access to the data
        final CourseDao courseDao = new CourseDao(getContext());
        final AnnouncementDao announcementDao = new AnnouncementDao(getContext());
        final AgendaDao agendaDao = new AgendaDao(getContext());

        // The request to sync the courses.
        CoursesMinervaRequest request = new CoursesMinervaRequest(getContext(), account);

        try {

            // If this is the first request, clean everything.
            if (isFirstSync) {
                agendaDao.deleteAll();
                announcementDao.deleteAll();
                courseDao.deleteAll();
            }

            Courses courses = request.performRequest();

            courseDao.synchronise(courses.getCourses());
            // Publish progress.
            broadcast.publishIntent(SyncBroadcast.SYNC_COURSES);

            // Synchronise the agenda.
            synchronizeAgenda(agendaDao, account);

            // Synchronize announcements for each course. There is no method to do this in one request.
            for (int i = 0; i < courses.getCourses().size(); i++) {
                Course course = courses.getCourses().get(i);
                if (isCancelled) {
                    broadcast.publishIntent(SyncBroadcast.SYNC_CANCELLED);
                    return;
                }

                Log.d(TAG, "Syncing course " + course.getId());

                // Sync announcements
                Collection<Announcement> newOnes = synchronizeAnnouncements(announcementDao, account, isFirstSync, course);

                // Publish progress
                broadcast.publishAnnouncementDone(i + 1, courses.getCourses().size(), course);

                // If not the first time, show notifications
                if (!isFirstSync) {
                    notifyUser(newOnes);
                }
            }

            broadcast.publishIntent(SyncBroadcast.SYNC_DONE);

        } catch (IOFailureException e) {
            Log.i(TAG, "IO error while syncing.", e);
            syncResult.stats.numIoExceptions++;
            syncErrorNotification(e);
        } catch (AuthenticatorActionException e) {
            Log.i(TAG, "Auth exception while syncing.", e);
            syncResult.stats.numAuthExceptions++;
            // This should not be null, but check it anyway.
            if (request.getAccountBundle() != null) {
                Intent intent = request.getAccountBundle().getParcelable(AccountManager.KEY_INTENT);
                SyncErrorNotification.Builder.init(getContext()).authError(intent).build().show();
                broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
            } else {
                syncErrorNotification(e);
            }
        } catch (RequestFailureException e) {
            Log.w(TAG, "Exception during sync:", e);
            // TODO: this needs attention.
            syncResult.stats.numParseExceptions++;
            syncErrorNotification(e);
        } catch (SQLException e) {
            Log.e(TAG, "Exception during sync:", e);
            syncResult.databaseError = true;
            syncErrorNotification(e);
        } catch (HttpMessageNotReadableException e) {
            Log.e(TAG, "Exception during sync:", e);
            syncResult.stats.numParseExceptions++;
            syncErrorNotification(e);
        }
    }

    /**
     * Show an error notification. This will also broadcast the error intent.
     */
    private void syncErrorNotification(Throwable throwable) {
        broadcast.publishIntent(SyncBroadcast.SYNC_ERROR);
        SyncErrorNotification.Builder.init(getContext()).genericError(throwable).build().show();
    }

    @Override
    public void onSyncCanceled() {
        super.onSyncCanceled();
        this.isCancelled = true;
    }

    /**
     * Show notifications to the user.
     *
     * @param newAnnouncements The announcements to show. Can be empty, but not null.
     */
    private void notifyUser(@NonNull Collection<Announcement> newAnnouncements) {

        if (newAnnouncements.isEmpty()) {
            return;
        }

        // If we may not notify the user, stop here.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!preferences.getBoolean(MinervaFragment.PREF_ANNOUNCEMENT_NOTIFICATION, true)) {
            return;
        }

        AnnouncementNotificationBuilder builder = new AnnouncementNotificationBuilder(getContext());
        builder.setAnnouncements(newAnnouncements);
        builder.setCourse(newAnnouncements.iterator().next().getCourse());
        builder.publish();
    }

    /**
     * Synchronize the agenda.
     *
     * @param account The Minerva-account.
     */
    private void synchronizeAgenda(AgendaDao dao, Account account) throws RequestFailureException {
        // Synchronise agenda
        AgendaRequest agendaRequest = new AgendaRequest(getContext(), account);
        ZonedDateTime now = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault());
        // Start time
        agendaRequest.setStart(now);
        // End time. We take 1 month (+1 day for the start time).
        agendaRequest.setEnd(now.plusMonths(1).plusDays(1));

        dao.replace(agendaRequest.performRequest().getItems());
        broadcast.publishIntent(SyncBroadcast.SYNC_AGENDA);
    }

    /**
     * Synchronize the announcements.
     */
    private Collection<Announcement> synchronizeAnnouncements(AnnouncementDao dao, Account account, boolean first, Course course) throws RequestFailureException {

        // First we get all courses. The dao marks the new ones as unread.
        AnnouncementsRequest announcementsRequest = new AnnouncementsRequest(getContext(), account, null, course);
        Announcements announcements = announcementsRequest.performRequest();

        WhatsNewRequest whatsNewRequest = new WhatsNewRequest(course, getContext(), account);
        WhatsNew whatsNew = whatsNewRequest.performRequest();

        // Construct object
        SyncObject object = new SyncObject.Builder(course)
                .allObjects(announcements.getAnnouncements())
                .newObjects(whatsNew.getAnnouncements())
                .setFirstSync(first)
                .build();

        //Sync announcements
        return dao.synchronize(object);
    }
}