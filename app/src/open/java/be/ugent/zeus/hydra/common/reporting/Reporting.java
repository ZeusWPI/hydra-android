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

package be.ugent.zeus.hydra.common.reporting;

import android.content.Context;
import androidx.annotation.VisibleForTesting;

/**
 * Produces the default tracker.
 *
 * @author Niko Strijbol
 */
public final class Reporting {

    private final static Object lock = new Object();
    private static Tracker tracker;

    /**
     * Get the default tracker.
     *
     * @param context The context.
     * @return The tracker.
     */
    public static Tracker getTracker(Context context) {
        synchronized (lock) {
            if (tracker == null) {
                tracker = new DummyTracker();
            }
        }
        return tracker;
    }

    public static BaseEvents getEvents() {
        return new DummyHolder();
    }

    @VisibleForTesting
    public static void setTracker(Tracker tracker) {
        Reporting.tracker = tracker;
    }

    public static boolean hasReportingOptions() {
        return false;
    }
}
