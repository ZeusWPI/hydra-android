/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
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
        return getAttr(context, attr, 0);
    }

    public static int getAttr(@NonNull Context context, int attr, int defaultAttr) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        if (value.resourceId != 0) {
            return value.resourceId;
        }
        return defaultAttr;
    }

    /**
     * @return a boolean value of {@code index}. If it does not exist, a boolean value of
     * {@code fallbackIndex}. If it still does not exist, {@code defaultValue}.
     */
    public static boolean getBoolean(@NonNull TypedArray a, @StyleableRes int index,
                                     @StyleableRes int fallbackIndex, boolean defaultValue) {
        boolean val = a.getBoolean(fallbackIndex, defaultValue);
        return a.getBoolean(index, val);
    }
}
