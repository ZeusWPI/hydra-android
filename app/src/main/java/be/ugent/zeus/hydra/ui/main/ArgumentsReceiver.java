package be.ugent.zeus.hydra.ui.main;

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
     * Called when the fragment is created by the hosting activity.
     *
     * @param activityIntent The intent of the activity.
     * @param existingArguments The bundle to put the arguments in.
     */
    void fillArguments(Intent activityIntent, Bundle existingArguments);
}