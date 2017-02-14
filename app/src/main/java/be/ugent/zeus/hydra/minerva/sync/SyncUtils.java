package be.ugent.zeus.hydra.minerva.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import be.ugent.zeus.hydra.minerva.auth.AccountUtils;

/**
 * @author Niko Strijbol
 */
public class SyncUtils {

    private static final String TAG = "SyncUtils";

    /**
     * Change the frequency of the periodic syncing if enabled.
     *
     * TODO: change this.
     *
     * @param context The context.
     * @param frequency The frequency.
     */
    public static void changeSyncFrequency(Context context, String authority, int frequency) {
        Account account = AccountUtils.getAccount(context);

        //If the account is not syncable, do not enable anything.
        if (ContentResolver.getSyncAutomatically(account, authority)) {
            Log.i(TAG, "Changing sync frequency of " + authority + " to " + frequency);
            //Remove the periodic sync
            ContentResolver.removePeriodicSync(account, authority, Bundle.EMPTY);
            //Re-insert the sync
            ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, frequency);
        }
    }

    /**
     * Enable the synchronisation for an authority.
     *
     * @param account The account.
     * @param authority The authority to enable.
     * @param freq The frequency to use.
     */
    public static void enable(Account account, String authority, int freq) {
        ContentResolver.setIsSyncable(account, authority, 1);
        ContentResolver.setSyncAutomatically(account, authority, true);
        ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, freq);
    }

    /**
     * Request a manual, expedited synchronisation.
     *  @param account The account to sync for.
     * @param authority The authority of the requested sync adapter.
     * @param extras The extras.
     */
    public static void requestSync(Account account, String authority, Bundle extras) {
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, authority, extras);
    }
}