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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class PriorityUtilsTest {

    @Test
    public void lerp() {

        //These tests only work for FEED_MAX_VALUE = 100;
        assertEquals(1000 + PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.FEED_MAX_VALUE);

        assertEquals(PriorityUtils.FEED_MAX_VALUE + PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.lerp(200, 0, 100));
        assertEquals(-10 + PriorityUtils.FEED_SPECIAL_SHIFT, PriorityUtils.lerp(-1, 0, 100));
    }

}