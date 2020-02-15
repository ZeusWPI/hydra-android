package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
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
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(attribute, typedValue, true);
        return typedValue.data;
    }
}
