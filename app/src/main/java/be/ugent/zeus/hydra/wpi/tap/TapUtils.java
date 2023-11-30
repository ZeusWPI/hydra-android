/*
 * Copyright (c) 2023 Niko Strijbol
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

package be.ugent.zeus.hydra.wpi.tap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

import be.ugent.zeus.hydra.common.network.Endpoints;

/**
 * Product from Tab
 *
 * @author Niko Strijbol
 */
public class TapUtils {

    private TapUtils() {
        // No.
    }

    @Nullable
    public static String createImageUrl(int id, @NonNull String urlTemplate, @Nullable String filename) {
        if (filename == null) {
            return null;
        }
        String paddedID = String.format(Locale.ROOT, "%09d", id);
        String first = paddedID.substring(0, 3);
        String second = paddedID.substring(3, 6);
        String third = paddedID.substring(6, 9);

        return Endpoints.TAP + String.format(Locale.ROOT, urlTemplate, first, second, third, filename);
    }
}
