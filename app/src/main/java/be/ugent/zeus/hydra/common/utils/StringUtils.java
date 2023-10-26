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

import android.icu.text.ListFormatter;
import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Locale;

/**
 * @author Niko Strijbol
 */
public class StringUtils {

    /**
     * Ensure the first letter is capitalised, while not touching the rest.
     *
     * @param s The string to capitalise.
     * @return Capitalised string.
     */
    public static String capitaliseFirst(@NonNull String s) {
        return s.substring(0, 1).toUpperCase(Locale.getDefault()) + s.substring(1);
    }

    /**
     * Format a set of elements into a textual list (e.g. "one, two and three").
     * <p> 
     * On supported Android versions, this will use the ICU library to get a
     * nicely formatted list. On older versions, the items will be joined using
     * a comma.
     * 
     * @param elements The items of the list.
     * @return The resulting list. Capitals are not touched.
     */
    public static String formatList(Collection<String> elements) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ListFormatter listFormatter = ListFormatter.getInstance();
            return listFormatter.format(elements);
        } else {
            return String.join(", ", elements);
        }
    }
}
