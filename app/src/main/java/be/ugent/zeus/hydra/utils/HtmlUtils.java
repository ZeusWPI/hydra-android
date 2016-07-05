package be.ugent.zeus.hydra.utils;

import android.os.Build;
import android.text.Html;

/**
 * @author Niko Strijbol
 */
public class HtmlUtils {

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
}
