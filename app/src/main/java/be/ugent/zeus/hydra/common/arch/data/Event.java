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

package be.ugent.zeus.hydra.common.arch.data;

import java.util.Optional;

/**
 * An event represents an one-off user action.
 * <p>
 * Typical use-cases are showing snack bars, toasts, etc.
 * <p>
 * Classes that listen to this will probably want to use the {@link be.ugent.zeus.hydra.common.arch.observers.EventObserver}.
 *
 * @author Niko Strijbol
 * @see <a href="https://goo.gl/JH3KxR">Medium article</a>
 */
public class Event<D> {

    private final D data;
    private boolean handled;

    public Event(D data) {
        this.data = data;
    }

    /**
     * Get the data if the data has not been handled yet. If the data has been handled, the optional will be empty.
     * If data has not been handled yet, it will now be considered handled.
     *
     * @return The data or not.
     */
    public Optional<D> handleData() {
        if (handled) {
            return Optional.empty();
        } else {
            handled = true;
            return Optional.ofNullable(data);
        }
    }
}
