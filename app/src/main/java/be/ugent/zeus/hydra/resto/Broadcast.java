package be.ugent.zeus.hydra.resto;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import be.ugent.zeus.hydra.resto.menu.slice.UpdatedMenuBroadcastReceiver;

/**
 * Utilities to indicate that the resto slice should be updated.
 *
 * @author Niko Strijbol
 */
public interface Broadcast {

    /**
     * Indicates the resto views should be updated; this is currently only used in the slices, but may
     * be used in the normal app in the future.
     */
    String ACTION_RESTO_UPDATE = "be.ugent.zeus.hydra.resto.update";

    int REQUEST_CODE = 555;

    static Intent createUpdateIntent(Context context) {
        Intent intent = new Intent(ACTION_RESTO_UPDATE);
        intent.setClass(context, UpdatedMenuBroadcastReceiver.class);
        return intent;
    }
}