package be.ugent.zeus.hydra.utils;

import android.support.annotation.ColorInt;
import android.support.v4.graphics.ColorUtils;

/**
 * @author Niko Strijbol
 */
public class ColourUtils {

    /**
     * Returns true if a colour is considered dark. A colour is dark if the light component is less than 0,6.
     *
     * @param colorInt The colour int.
     *
     * @return True if dark, false otherwise.
     */
    public static boolean isDark(@ColorInt int colorInt) {
        return ColorUtils.calculateLuminance(colorInt) < 0.6f;
    }
}