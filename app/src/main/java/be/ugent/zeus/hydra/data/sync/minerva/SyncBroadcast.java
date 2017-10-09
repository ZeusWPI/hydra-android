package be.ugent.zeus.hydra.data.sync.minerva;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Class to manage sending broadcasts from the sync adapter. These are local broadcasts.
 *
 * @author Niko Strijbol
 */
public class SyncBroadcast {

    /**
     * The synchronisation has started.
     */
    public static final String SYNC_START = "be.ugent.zeus.hydra.data.sync.start";
    /**
     * The synchronisation was completed successfully.
     */
    public static final String SYNC_DONE = "be.ugent.zeus.hydra.data.sync.done";
    /**
     * The synchronisation has halted because of an error.
     */
    public static final String SYNC_ERROR = "be.ugent.zeus.hydra.data.sync.error";
    /**
     * The synchronisation has halted because of a cancel request.
     */
    public static final String SYNC_CANCELLED = "be.ugent.hydra.minerva.sync.cancelled";
    /**
     * The synchronisation has progressed and the list of courses itself has been loaded.
     */
    public static final String SYNC_COURSES = "be.ugent.hydra.zeus.minerva.sync.courses";
    /**
     * The synchronisation has progressed and the Minerva agenda has been synchronised.
     */
    public static final String SYNC_AGENDA = "be.ugent.hydra.zeus.minerva.sync.agenda";
    /**
     * The synchronisation has progressed and information for a course has been loaded.
     */
    public static final String SYNC_PROGRESS_WHATS_NEW = "be.ugent.hydra.zeus.minerva.sync.course_data";
    /**
     * Argument for {@link #SYNC_PROGRESS_WHATS_NEW}, the total number of things to load.
     */
    public static final String ARG_SYNC_PROGRESS_TOTAL = "argTotal";
    /**
     * Argument for {@link #SYNC_PROGRESS_WHATS_NEW}, the current number the loaded item.
     */
    public static final String ARG_SYNC_PROGRESS_CURRENT = "argNow";
    /**
     * Argument for {@link #SYNC_PROGRESS_WHATS_NEW}, the current course id.
     */
    public static final String ARG_SYNC_PROGRESS_COURSE = "argCourse";

    private final LocalBroadcastManager broadcastManager;

    /**
     * @param context A context that sends the broadcasts.
     */
    public SyncBroadcast(Context context) {
        broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    /**
     * Publish an intent.
     *
     * @param action The intent action to publish.
     */
    public void publishIntent(String action) {
        publishIntentWith(action, null);
    }

    private void publishIntentWith(String action, Bundle extras) {
        //The intent
        Intent i = new Intent(action);
        if (extras != null) {
            i.putExtras(extras);
        }

        broadcastManager.sendBroadcast(i);
    }

    public void publishAnnouncementDone(int now, int total) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SYNC_PROGRESS_TOTAL, total);
        bundle.putInt(ARG_SYNC_PROGRESS_CURRENT, now);
        publishIntentWith(SYNC_PROGRESS_WHATS_NEW, bundle);
    }

    /**
     * @return A filterIntent that filters synchronisation broadcasts.
     */
    public static IntentFilter getBroadcastFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SYNC_START);
        filter.addAction(SYNC_COURSES);
        filter.addAction(SYNC_AGENDA);
        filter.addAction(SYNC_PROGRESS_WHATS_NEW);
        filter.addAction(SYNC_DONE);
        filter.addAction(SYNC_ERROR);
        filter.addAction(SYNC_CANCELLED);
        return filter;
    }
}