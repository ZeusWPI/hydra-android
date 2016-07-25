package be.ugent.zeus.hydra.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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
     * Get a drawable (also works for vectors) in the given color.
     *
     * @param context A context
     * @param drawable The drawable to get
     * @param color The color to tint the drawable in
     * @return The drawable
     */
    public static Drawable getTintedDrawable(Context context, @DrawableRes int drawable, @ColorRes int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Drawable d = context.getDrawable(drawable);
            assert d != null;
            d.setTint(context.getColor(color));
            return d;
        } else {
            Drawable d = ContextCompat.getDrawable(context, drawable);
            Drawable dw = DrawableCompat.wrap(d);
            DrawableCompat.setTint(dw, ContextCompat.getColor(context, color));
            return dw;
        }
    }
}