package be.ugent.zeus.hydra.data.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import be.ugent.zeus.hydra.data.auth.AccountUtils;
import be.ugent.zeus.hydra.ui.preferences.MinervaFragment;

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
     * @param context   The context.
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
     *  @param account   The account.
     * @param authority The authority to enable.
     * @param freq      The frequency to use.
     */
    public static void enable(Account account, String authority, long freq) {
        ContentResolver.setIsSyncable(account, authority, 1);
        ContentResolver.setSyncAutomatically(account, authority, true);
        ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, freq);
    }

    /**
     * Request a manual, expedited synchronisation.
     *
     * @param account   The account to sync for.
     * @param authority The authority of the requested sync adapter.
     * @param arguments The extras. Can be {@link Bundle#EMPTY}, but not null.
     */
    public static void requestSync(Account account, String authority, Bundle arguments) {
        Bundle extras = new Bundle(arguments);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, authority, extras);
    }

    /**
     * Get the synchronisation frequency from the preferences for a given authority. This function is the main
     * mapping between the preference keys and the authorities.
     *
     * This method throws an {@link IllegalArgumentException} if an unknown authority is used. This is always a
     * programming error.
     *
     * @param context The context.
     * @return The sync frequency.
     */
    public static long frequencyFor(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String prefKey = MinervaFragment.PREF_SYNC_FREQUENCY;
        String prefDefault = MinervaFragment.PREF_DEFAULT_SYNC_FREQUENCY;

        String pref = preferences.getString(prefKey, prefDefault);
        return Long.parseLong(pref);
    }
}