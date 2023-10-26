/*
 * Copyright (c) 2022 The Hydra authors
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.core.graphics.ColorUtils;

/**
 * @author Niko Strijbol
 */
public class ColourUtils {

    /**
     * Returns true if a colour is considered dark. A colour is dark if the light component is less than 0,6.
     *
     * @param colorInt The colour int.
     * @return True if dark, false otherwise.
     */
    public static boolean isDark(@ColorInt int colorInt) {
        return ColorUtils.calculateLuminance(colorInt) < 0.6f;
    }

    @ColorInt
    public static int resolveColour(Context context, @AttrRes int attribute) {
        int[] attrs = {attribute};
        //noinspection resource
        @SuppressLint("ResourceType") // False positive I think.
        TypedArray ta = context.obtainStyledAttributes(attrs);
        try {
            return ta.getColor(0, 0);
        } finally {
            ta.recycle();
        }
    }
}
