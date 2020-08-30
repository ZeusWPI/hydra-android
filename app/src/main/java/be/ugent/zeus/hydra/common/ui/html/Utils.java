package be.ugent.zeus.hydra.common.ui.html;

import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;

/**
 * @author Niko Strijbol
 */
public class Utils {

    private static final String TAG = "HtmlUtils";

    /**
     * Helper with older version support. If the html is null, an empty Spannable will be returned.
     *
     * @param html   The HTML to strip.
     * @param getter The image getter. I suggest using {@link PicassoImageGetter} if you don't need anything special.
     *
     * @return Stripped HTML.
     */
    public static Spanned fromHtml(String html, Html.ImageGetter getter) {

        if (html == null) {
            return new SpannableString("");
        }

        if (Build.VERSION.SDK_INT < 24) {
            try {
                //noinspection deprecation
                return Html.fromHtml(html, getter, new HtmlTagHandler());
            } catch (RuntimeException e) {
                // Older versions crash sometimes, so try again without custom tags.
                Log.e(TAG, "Error while reading html.", e);
                //noinspection deprecation
                return Html.fromHtml(html, getter, null);
            }
        } else {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, getter, new HtmlTagHandler());
        }
    }

    /**
     * @see #fromHtml(String, Html.ImageGetter)
     */
    public static Spanned fromHtml(String html) {
        return fromHtml(html, null);
    }
}