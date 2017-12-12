package be.ugent.zeus.hydra.ui.main;

import android.content.Intent;

/**
 * Allows a fragment to be notified of new intents.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface OnReselectListener {

    /**
     * Called when the main activity's {@link android.app.Activity#onNewIntent(Intent)} is called with an intent
     * that selects the current fragment. This intent may contain updated arguments for the fragment, which is why the
     * fragment should implement this listener.
     *
     * After this method has been called, the fragment should be in the same state as if it was initialised using the
     * arguments in the provided {@code intent}. Of course, if the fragment does not rely on arguments to initialise,
     * it is not necessary to implement this or do anything in this method.
     *
     * @param intent The new intent.
     */
    void onReselect(Intent intent);
}
