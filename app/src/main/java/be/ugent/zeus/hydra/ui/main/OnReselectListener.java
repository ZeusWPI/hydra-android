package be.ugent.zeus.hydra.ui.main;

import android.content.Intent;

/**
 * Allows a fragment to be notified of new intents.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface OnReselectListener {

    void onReselect(Intent intent);
}
