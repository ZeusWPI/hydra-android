package be.ugent.zeus.hydra.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.content.res.AppCompatResources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import be.ugent.zeus.hydra.R;

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
    @NonNull
    public static <T extends View> T $(View view, @IdRes int id) {
        @SuppressWarnings("unchecked")
        T v = (T) view.findViewById(id);
        assert v != null;
        return v;
    }

    public static int getPrimaryColor(Activity activity) {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * Get a vector in the given color.
     *
     * @param context A context
     * @param drawable The drawable to get
     * @param color The color to tint the drawable in
     * @return The drawable
     */
    public static Drawable getTintedVectorDrawable(Context context, @DrawableRes int drawable, @ColorRes int color) {

        Drawable d = AppCompatResources.getDrawable(context, drawable);
        DrawableCompat.setTint(d, ContextCompat.getColor(context, color));

        return d;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static int convertDpToPixelInt(float dp, Context context){
        return (int) convertDpToPixel(dp, context);
    }

}