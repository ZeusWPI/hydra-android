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

package be.ugent.zeus.hydra.association.list;

import android.net.Uri;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class FilterTest {
    
    @Test
    public void urlGeneration() {
        OffsetDateTime now = OffsetDateTime.now();
        Filter.Live filter = new Filter.Live();
        filter.setWhitelist(new HashSet<>(Arrays.asList("test", "hallo")));
        filter.setTerm("search");
        filter.setAfter(now);
        filter.setBefore(now);

        Uri.Builder builder = Uri.parse("http://example.com").buildUpon();
        filter.filter.appendParams(builder);
        Uri uri = builder.build();
        
        assertEquals("search", uri.getQueryParameter("search_string"));
        Set<String> expected = Stream.of("test", "hallo").collect(Collectors.toSet());
        Set<String> actual = new HashSet<>(uri.getQueryParameters("association[]"));
        assertEquals(expected, actual);
        assertEquals(now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), uri.getQueryParameter("start_time"));
        assertEquals(now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME), uri.getQueryParameter("end_time"));
    }

}