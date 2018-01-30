package be.ugent.zeus.hydra.main;

import android.content.Intent;
import android.os.Bundle;

/**
 * Allow fragments to extract arguments from the activity.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface ArgumentsReceiver {

    /**
     * Called when the fragment is created by the hosting activity. Allows the fragment to extract arguments from the
     * {@code activityIntent} and put them in {@code existingArguments}. The resulting bundle will then eventually
     * be set as the arguments of the fragment.
     *
     * This function should be a pure function, meaning there should be no side effects in the fragment. Side-effects
     * resulting from this function may cause undefined behaviour.
     *
     * @param activityIntent The intent of the activity.
     * @param existingArguments The bundle to put the arguments in.
     */
    void fillArguments(Intent activityIntent, Bundle existingArguments);
}