package be.ugent.zeus.hydra.utils.html;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 * @author Niko Strijbol
 */
public class Utils {

    /**
     * Strip HTML.
     *
     * TODO this code is not very performant
     *
     * @param html The HTML to strip.
     *
     * @return Stripped HTML.
     */
    public static CharSequence stripHtml(String html) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //noinspection deprecation
            return Html.fromHtml(html).toString();
        } else {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString();
        }
    }

    /**
     * Helper with older version support.
     *
     * @param html The HTML to strip.
     * @param getter The image getter. I suggest using {@link PicassoImageGetter} if you don't need anything special.
     *
     * @return Stripped HTML.
     */
    public static Spanned fromHtml(String html, Html.ImageGetter getter) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            //noinspection deprecation
            return Html.fromHtml(html, getter, null);
        } else {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, getter, null);
        }
    }

    public static Spanned fromHtml(String html) {
        return fromHtml(html, null);
    }
}
