package be.ugent.zeus.hydra.minerva.sync;

import android.accounts.Account;
import android.content.*;
import android.os.Bundle;

/**
 * Class to manage sending broadcasts from the sync adapter.
 *
 * @author Niko Strijbol
 */
public class SyncBroadcast {

    /**
     * The synchronisation has started.
     */
    public static final String SYNC_START = "be.ugent.zeus.minerva.broadcast.start";
    /**
     * The synchronisation was completed successfully.
     */
    public static final String SYNC_DONE = "be.ugent.zeus.minerva.broadcast.done";
    /**
     * The synchronisation has halted because of an error.
     */
    public static final String SYNC_ERROR = "be.ugent.zeus.minerva.broadcast.error";
    /**
     * The synchronisation has halted because of a cancel request.
     */
    public static final String SYNC_CANCELLED = "be.ugent.minerva.broadcast.cancelled";
    /**
     * The synchronisation has progressed and has loaded the list of courses.
     */
    public static final String SYNC_PROGRESS_COURSES = "be.ugent.zeus.minerva.broadcast.progress.courses";
    /**
     * The synchronisation has progressed and information for a course has been loaded.
     */
    public static final String SYNC_PROGRESS_WHATS_NEW = "be.ugent.zeus.minerva.broadcast.progress.data";
    /**
     * Argument for {@link #SYNC_PROGRESS_WHATS_NEW}, the total number of things to load.
     */
    public static final String ARG_SYNC_PROGRESS_TOTAL = "argTotal";
    /**
     * Argument for {@link #SYNC_PROGRESS_WHATS_NEW}, the current number the loaded item.
     */
    public static final String ARG_SYNC_PROGRESS_CURRENT = "argNow";

    private final Context context;
    private Bundle args;

    /**
     * @param context A context that sends the broadcasts.
     */
    public SyncBroadcast(Context context) {
        this.context = context;
    }

    /**
     * @param args The argument bundle passed to {@link SyncAdapter#onPerformSync(Account, Bundle, String, ContentProviderClient, SyncResult)}.
     */
    public void setArgs(Bundle args) {
        this.args = args;
    }

    private boolean canPublish() {
        if(args == null) {
            throw new IllegalStateException("The arguments cannot be null.");
        }

        return args.getBoolean(SyncAdapter.ARG_SEND_BROADCASTS, false);
    }

    /**
     * Publish an intent.
     *
     * @param action The intent action to publish.
     */
    void publishIntent(String action) {
        publishIntentWith(action, null);
    }

    private void publishIntentWith(String action, Bundle extras) {
        //Don't send if we don't need to.
        if(!canPublish()) {
            return;
        }

        //The intent
        Intent i = new Intent(action);
        if(extras != null) {
            i.putExtras(extras);
        }

        context.sendBroadcast(i);
    }

    void publishAnnouncementDone(int now, int total) {
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
        filter.addAction(SYNC_PROGRESS_COURSES);
        filter.addAction(SYNC_PROGRESS_WHATS_NEW);
        filter.addAction(SYNC_DONE);
        filter.addAction(SYNC_ERROR);
        filter.addAction(SYNC_CANCELLED);
        return filter;
    }
}