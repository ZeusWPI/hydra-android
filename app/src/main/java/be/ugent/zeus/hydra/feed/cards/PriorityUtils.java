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

package be.ugent.zeus.hydra.feed.cards;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;
import androidx.preference.PreferenceManager;

import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import be.ugent.zeus.hydra.feed.preferences.HomeFragment;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Util class, with e.g. help calculate the priority a card should have in the home feed.
 *
 * @author Niko Strijbol
 * @see Card
 */
public class PriorityUtils {

    /**
     * The special shift.
     */
    public static final int FEED_SPECIAL_SHIFT = 10;

    /**
     * The upper limit on the feed.
     */
    public static final int FEED_MAX_VALUE = 1000 + FEED_SPECIAL_SHIFT;

    private PriorityUtils() {
    }

    /**
     * Lineair interpolation of the value x ∈ [a,b] to [FEED_SPECIAL_SHIFT,FEED_MAX_VALUE]. The formula used is:
     * <p>
     * x' = (c - a) * FEED_MAX_VALUE / (b - a)
     * <p>
     * If x is bigger than b, FEED_MAX_VALUE is returned. This is to help calculate a correct priority.
     *
     * @param x The value in the original range.
     * @param a Original range start.
     * @param b Original range end.
     * @return The interpolated value in [y,z].
     */
    public static int lerp(int x, int a, int b) {
        return Math.min((int) ((x - a) * (double) FEED_MAX_VALUE / (b - a)), FEED_MAX_VALUE) + FEED_SPECIAL_SHIFT;
    }

    private static boolean isDataConstrained(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean setting = preferences.getBoolean(HomeFragment.PREF_DATA_SAVER, HomeFragment.PREF_DATA_SAVER_DEFAULT);
        return setting && NetworkUtils.isMeteredConnection(context);
    }

    /**
     * Use Picasso to load a thumbnail. If data saving is enabled, the thumbnail will only be loaded from the cache.
     *
     * @param context The context.
     * @param image   The image url to load.
     * @param target  The target view for the image.
     */
    public static void loadThumbnail(Context context, String image, ImageView target) {
        RequestCreator creator = Picasso.get().load(image).fit().centerInside();

        if (PriorityUtils.isDataConstrained(context)) {
            creator.networkPolicy(NetworkPolicy.OFFLINE);
        }

        creator.into(target);
    }
}
