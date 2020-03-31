package be.ugent.zeus.hydra.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.*;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * @author Niko Strijbol
 */
public class ViewUtils {

    private ViewUtils() {
    }

    /**
     * Get a vector in the given attribute, which is resolved to a colour.
     *
     * @param context   A context.
     * @param drawable  The drawable to tint.
     * @param attribute The attribute to tint the drawable in.
     *
     * @return The drawable.
     */
    public static Drawable
    getTintedVectorDrawableAttr(Context context, @DrawableRes int drawable, @AttrRes int attribute) {
        int color = ColourUtils.resolveColour(context, attribute);
        return getTintedVectorDrawableInt(context, drawable, color);
    }

    /**
     * Get a vector in the given colour.
     *
     * @param context  A context
     * @param drawable The drawable to tint.
     * @param color    The color int to tint the drawable in.
     *
     * @return The drawable.
     */
    @Nullable
    public static Drawable getTintedVectorDrawableInt(Context context, @DrawableRes int drawable, @ColorInt int color) {

        if (drawable == 0) {
            return null;
        }
        Drawable d = AppCompatResources.getDrawable(context, drawable);
        assert d != null;
        DrawableCompat.setTint(d, color);

        return d;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     *
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * Same as {@link #convertDpToPixel(float, Context)}, but casted to an int.
     */
    public static int convertDpToPixelInt(float dp, Context context) {
        return (int) convertDpToPixel(dp, context);
    }

    /**
     * Shorter function for inflating a view without attaching it to the parent.
     *
     * @param parent   The parent layout.
     * @param resource The XML resource to inflate.
     *
     * @return The view.
     */
    public static View inflate(ViewGroup parent, @LayoutRes int resource) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(resource, parent, false);
    }

    /**
     * @return The resource ID value in the {@code context} specified by {@code attr}.
     */
    public static int getAttr(@NonNull Context context, int attr) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        return value.resourceId;
    }
}
