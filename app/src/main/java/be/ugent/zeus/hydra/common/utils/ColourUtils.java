package be.ugent.zeus.hydra.common.utils;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.core.graphics.ColorUtils;

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

    @ColorInt
    public static int resolveColour(Context context, @AttrRes int attribute) {
        int[] attrs = {attribute};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        try {
            return ta.getColor(0, 0);
        } finally {
            ta.recycle();
        }
    }
}
