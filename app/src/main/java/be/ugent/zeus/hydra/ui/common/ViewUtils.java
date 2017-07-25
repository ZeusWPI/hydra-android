package be.ugent.zeus.hydra.ui.common;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.*;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import be.ugent.zeus.hydra.R;

/**
 * @author Niko Strijbol
 */
@SuppressWarnings("WeakerAccess")
public class ViewUtils {

    /**
     * Finds a view that was identified by the id attribute from the XML that was processed in. This
     * version automatically casts the return value. This functions also assumes the element exists (and is
     * not null). This cannot be used for elements that may or may not be present.
     *
     * @return The view.
     */
    @NonNull
    public static <T extends View> T $(View view, @IdRes int id) {
        T v = view.findViewById(id);
        assert v != null;
        return v;
    }

    @ColorInt
    public static int getPrimaryColor(Context context) {
        return getColor(context, R.attr.colorPrimary);
    }

    @ColorInt
    public static int getColor(Context context, @AttrRes int attribute) {

        int[] attrs = new int[]{attribute};
        TypedArray ta = context.obtainStyledAttributes(attrs);

        try {
            return ta.getColor(0, 0);
        } finally {
            ta.recycle();
        }
    }

    /**
     * Get a vector in the given color.
     *
     * @param context  A context.
     * @param drawable The drawable to get.
     * @param color    The color to tint the drawable in.
     *
     * @return The drawable.
     */
    public static Drawable getTintedVectorDrawable(Context context, @DrawableRes int drawable, @ColorRes int color) {
        return getTintedVectorDrawableInt(context, drawable, ContextCompat.getColor(context, color));
    }

    /**
     * Get a vector in the given color.
     *
     * @param context  A context
     * @param drawable The drawable to get
     * @param color    The color int to tint the drawable in.
     *
     * @return The drawable.
     */
    public static Drawable getTintedVectorDrawableInt(Context context, @DrawableRes int drawable, @ColorInt int color) {

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
     * @param parent The parent layout.
     * @param resource The XML resource to inflate.
     * @return The view.
     */
    public static View inflate(ViewGroup parent, @LayoutRes int resource) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return inflater.inflate(resource, parent, false);
    }
}