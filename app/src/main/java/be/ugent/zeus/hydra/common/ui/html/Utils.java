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