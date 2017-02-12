package be.ugent.zeus.hydra.minerva.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import be.ugent.zeus.hydra.minerva.announcement.sync.Adapter;
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
     * @param context The context.
     * @param frequency The frequency.
     */
    public static void changeSyncFrequency(Context context, int frequency) {
        Account account = AccountUtils.getAccount(context);

        //If the account is not syncable, do not enable anything.
        if (ContentResolver.getSyncAutomatically(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY) && ContentResolver.getSyncAutomatically(account, MinervaConfig.CALENDAR_AUTHORITY)) {
            Log.i(TAG, "Changing sync frequency to " + frequency);
            //Remove the periodic sync
            ContentResolver.removePeriodicSync(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY, Bundle.EMPTY);
            ContentResolver.removePeriodicSync(account, MinervaConfig.CALENDAR_AUTHORITY, Bundle.EMPTY);
            //Re-add the sync
            ContentResolver.addPeriodicSync(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY, Bundle.EMPTY, frequency);
            ContentResolver.addPeriodicSync(account, MinervaConfig.CALENDAR_AUTHORITY, Bundle.EMPTY, frequency);
        }
    }

    /**
     * Enable periodic synchronisation for a given Minerva account. This will enable periodic syncing using
     * the frequency from the settings.
     *
     * @param context The context.
     * @param account The account.
     */
    public static void enableSync(Context context, Account account) {
        ContentResolver.setIsSyncable(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY, true);
        ContentResolver.setIsSyncable(account, MinervaConfig.CALENDAR_AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, MinervaConfig.CALENDAR_AUTHORITY, true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int frequency = Integer.valueOf(preferences.getString("pref_minerva_sync_frequency", "86400"));

        ContentResolver.addPeriodicSync(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY, Bundle.EMPTY, frequency);
        ContentResolver.addPeriodicSync(account, MinervaConfig.CALENDAR_AUTHORITY, Bundle.EMPTY, frequency);
    }

    /**
     * Request the first sync for an account. The sync will happen right now.
     *
     * @param account The account.
     */
    public static void requestFirstSync(Account account) {
        Log.d(TAG, "Requesting first sync...");
        Bundle bundle = new Bundle();
        bundle.putBoolean(MinervaAdapter.EXTRA_FIRST_SYNC, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, MinervaConfig.ANNOUNCEMENT_AUTHORITY, bundle);
        ContentResolver.requestSync(account, MinervaConfig.CALENDAR_AUTHORITY, bundle);
    }
}