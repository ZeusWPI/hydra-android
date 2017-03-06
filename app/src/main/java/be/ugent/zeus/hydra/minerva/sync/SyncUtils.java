package be.ugent.zeus.hydra.minerva.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import be.ugent.zeus.hydra.fragments.preferences.MinervaFragment;
import be.ugent.zeus.hydra.minerva.auth.AccountUtils;
import be.ugent.zeus.hydra.minerva.auth.MinervaConfig;

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
     * @param extras    The extras.
     */
    public static void requestSync(Account account, String authority, Bundle extras) {
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
     * @param authority The authority for the sync frequency.
     *
     * @return The sync frequency.
     */
    public static long frequencyFor(Context context, String authority) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        String prefKey;
        String prefDefault;

        switch (authority) {
            case MinervaConfig.ANNOUNCEMENT_AUTHORITY:
                prefKey = MinervaFragment.PREF_SYNC_FREQUENCY_ANNOUNCEMENT;
                prefDefault = MinervaFragment.PREF_DEFAULT_SYNC_FREQUENCY;
                break;
            case MinervaConfig.CALENDAR_AUTHORITY:
                prefKey = MinervaFragment.PREF_SYNC_FREQUENCY_CALENDAR;
                prefDefault = MinervaFragment.PREF_DEFAULT_SYNC_FREQUENCY;
                break;
            case MinervaConfig.COURSE_AUTHORITY:
                prefKey = MinervaFragment.PREF_SYNC_FREQUENCY_COURSE;
                prefDefault = MinervaFragment.PREF_DEFAULT_SYNC_LONG_FREQUENCY;
                break;
            default:
                throw new IllegalArgumentException("Unknown authority: " + authority);
        }

        String pref = preferences.getString(prefKey, prefDefault);
        return Long.parseLong(pref);
    }
}