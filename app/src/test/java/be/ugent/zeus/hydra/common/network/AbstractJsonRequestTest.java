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

package be.ugent.zeus.hydra.common.network;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okio.BufferedSource;
import okio.Okio;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Base class for testing Json requests. This base class provides the test to see if the request parses the data
 * correctly.
 *
 * @author Niko Strijbol
 */
public abstract class AbstractJsonRequestTest<D> {

    protected Moshi moshi;
    protected Context context;

    protected static String readData(File file) throws IOException {
        BufferedSource source = Okio.buffer(Okio.source(new FileInputStream(file)));
        return source.readUtf8();
    }

    @Before
    public void setUp() {
        InstanceProvider.reset();
        moshi = InstanceProvider.moshi();
        context = ApplicationProvider.getApplicationContext();
    }

    protected final File getResourceFile(String resourcePath) {
        return new File(getClass().getClassLoader().getResource(resourcePath).getFile());
    }

    protected abstract String getRelativePath();

    /**
     * @return The request that should be tested.
     */
    protected abstract JsonOkHttpRequest<D> getRequest();

    protected D getExpectedResult(String data) throws IOException {
        JsonAdapter<D> adapter = moshi.adapter(getRequest().typeToken());
        return adapter.fromJson(data);
    }

    @Test
    public void testValidUrl() {
        JsonOkHttpRequest<?> request = getRequest();
        UrlValidator validator = new UrlValidator();
        assertTrue(validator.isValid(request.apiUrl()));
    }
}
