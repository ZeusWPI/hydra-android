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

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

/**
 * An analytics event. This event is implementation-independent; at the same time the values are defined by the
 * implementation itself.
 * <p>
 * A selection of predefined types is available in {@link BaseEvents}. Get an implementation from the {@link
 * Reporting}.
 *
 * @author Niko Strijbol
 */
public interface Event {

    /**
     * @return Parameters to be included in the log for this event. The implementation might impose restrictions, such
     * as an enumeration. If {@link #getEventName()} returns {@code null}, the return value of this method is
     * undefined.
     */
    @Nullable
    default Bundle getParams() {
        return null;
    }

    /**
     * @return The name of the event for the log. The implementation might impose restrictions, such as predefined
     * names, name length or name uniqueness. If {@code null}, the event will not be logged.
     */
    @Nullable
    String getEventName();

    /**
     * @return The name of the event for the log. The implementation might impose restrictions, such as predefined
     * names, name length or name uniqueness. If not present, it will not be logged.
     */
    @NonNull
    default Optional<String> getEvent() {
        return Optional.ofNullable(getEventName());
    }
}
