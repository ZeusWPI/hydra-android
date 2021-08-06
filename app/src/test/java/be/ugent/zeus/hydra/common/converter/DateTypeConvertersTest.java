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

package be.ugent.zeus.hydra.common.converter;

import java.time.Instant;
import java.time.OffsetDateTime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Niko Strijbol
 */
public class DateTypeConvertersTest {

    @Test
    public void toOffsetDateTime() {
        OffsetDateTime now = OffsetDateTime.now();
        String expected = now.format(DateTypeConverters.OFFSET_FORMATTER);
        assertEquals(expected, DateTypeConverters.fromOffsetDateTime(now));
    }

    @Test
    public void toOffsetDateTimeNull() {
        assertNull(DateTypeConverters.fromOffsetDateTime(null));
    }

    @Test
    public void fromOffsetDateTime() {
        OffsetDateTime now = OffsetDateTime.now();
        String value = now.format(DateTypeConverters.OFFSET_FORMATTER);
        assertEquals(now, DateTypeConverters.toOffsetDateTime(value));
    }

    @Test
    public void fromOffsetDateTimeNull() {
        assertNull(DateTypeConverters.toOffsetDateTime(null));
    }

    @Test
    public void fromInstant() {
        Instant now = Instant.now();
        assertEquals(now.toString(), DateTypeConverters.fromInstant(now));
    }

    @Test
    public void fromInstantNull() {
        //noinspection ConstantConditions
        assertNull(DateTypeConverters.fromInstant(null));
    }

    @Test
    public void toInstant() {
        Instant now = Instant.now();
        assertEquals(now, DateTypeConverters.toInstant(now.toString()));
    }

    @Test
    public void toInstantNull() {
        assertNull(DateTypeConverters.toInstant(null));
    }
}
