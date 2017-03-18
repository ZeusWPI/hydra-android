package be.ugent.zeus.hydra.data.database.minerva;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Helper class that broadcasts intents when things have changed in the database.
 *
 * Note that changes due to synchronisation are currently NOT broadcast; see {@link be.ugent.zeus.hydra.data.sync.SyncBroadcast}.
 * This might change in the future.
 *
 * @author Niko Strijbol
 */
public class DatabaseBroadcaster {

    private final LocalBroadcastManager broadcastManager;

    /**
     * Broadcast when an announcement is updated in any way.
     */
    public final static String MINERVA_ANNOUNCEMENT_UPDATED = "be.ugent.zeus.hydra.minerva.db.course.update";

    /**
     * The ID of the updated announcement. Used by {@link #MINERVA_ANNOUNCEMENT_UPDATED}. This is an integer.
     */
    public final static String ARG_MINERVA_ANNOUNCEMENT_ID = "argAnnouncement.";

    /**
     * The ID of the course of the updated announcement. Used by {@link #MINERVA_ANNOUNCEMENT_UPDATED}. This is a string.
     */
    public final static String ARG_MINERVA_ANNOUNCEMENT_COURSE = "argCourse";


    public DatabaseBroadcaster(Context context) {
        this.broadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public void publishIntentWith(String action, Bundle extras) {
        //The intent
        Intent i = new Intent(action);
        if (extras != null) {
            i.putExtras(extras);
        }

        broadcastManager.sendBroadcast(i);
    }
}