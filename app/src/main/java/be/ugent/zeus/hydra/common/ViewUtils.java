package be.ugent.zeus.hydra.common;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * @author Niko Strijbol
 * @version 31/05/2016
 */
public class ViewUtils {

    /**
     * Finds a view that was identified by the id attribute from the XML that was processed in. This
     * version automatically casts the return value. This functions also assumes the element exists (and is
     * not null). This cannot be used for elements that may or may not be present.
     *
     * @return The view.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends View> T $(View view, @IdRes int id) {
        return (T) view.findViewById(id);
    }
}