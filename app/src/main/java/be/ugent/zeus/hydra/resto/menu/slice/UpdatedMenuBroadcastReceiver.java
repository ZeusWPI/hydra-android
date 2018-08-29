package be.ugent.zeus.hydra.resto.menu.slice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import be.ugent.zeus.hydra.resto.Broadcast;
import org.threeten.bp.LocalDate;

/**
 * This broadcast receiver is registered to listen to {@link be.ugent.zeus.hydra.resto.Broadcast#ACTION_RESTO_UPDATE}
 * events to update any potential slices.
 *
 * @author Niko Strijbol
 */
public class UpdatedMenuBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "UpdatedMenuBroadcastRec";

    public static final String EXTRA_DATE_UPDATE = "args_date_update";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check the intent action to be sure.
        Log.i(TAG, "onReceive: received broadcast" + intent.toString());
        if (!Broadcast.ACTION_RESTO_UPDATE.equals(intent.getAction())) {
            return;
        }

        Log.i(TAG, "Updating resto slice uri.");

        // Update the date of the slice if necessary.
        if (intent.hasExtra(EXTRA_DATE_UPDATE)) {
            Log.i(TAG, "Updating the menu date of the slice, new date is " + intent.getStringExtra(EXTRA_DATE_UPDATE));
            String date = intent.getStringExtra(EXTRA_DATE_UPDATE);
            DayManager.setDate(LocalDate.parse(date));
        }

        // Update the slice itself.
        Uri uri = Uri.parse(SliceProvider.FULL_URI);
        context.getContentResolver().notifyChange(uri, null);
    }
}