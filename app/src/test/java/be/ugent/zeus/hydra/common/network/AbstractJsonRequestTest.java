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

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;
import org.apache.commons.validator.routines.UrlValidator;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

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
        moshi = InstanceProvider.getMoshi();
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
        JsonAdapter<D> adapter = moshi.adapter(getRequest().getTypeToken());
        return adapter.fromJson(data);
    }

    @Test
    public void testValidUrl() {
        JsonOkHttpRequest<?> request = getRequest();
        UrlValidator validator = new UrlValidator();
        assertTrue(validator.isValid(request.getAPIUrl()));
    }

    @Test
    public void testNormal() throws IOException, JSONException, RequestException {

        JsonOkHttpRequest<D> request = getRequest();
        File resource = getResourceFile(getRelativePath());
        String data = readData(resource);

        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.enqueue(new MockResponse()
                .setBody(data)
                .addHeader("Content-Type", "application/json; charset=utf-8")
        );
        mockWebServer.start();

        // Override the URL using Mockito. Not really pretty, but it is reliable and works.
        HttpUrl originalUrl = HttpUrl.parse(request.getAPIUrl());
        assertNotNull(originalUrl);
        HttpUrl serverUrl = mockWebServer.url(originalUrl.encodedPath());
        request = spyForNormal(request);
        doReturn(serverUrl.toString()).when(request).getAPIUrl();

        Result<D> result = request.execute();

        // Throw the error if present for better error reporting.
        if (result.hasException()) {
            throw result.getError();
        }

        assertTrue(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.isWithoutError());

        // Test that all data is present by getting the normal data first.

        // Write the parsed data back to json and parse that.
        JsonAdapter<D> adapter = request.getAdapter().serializeNulls();
        String actualData = adapter.toJson(result.getData());

        JSONAssert.assertEquals(data, actualData, false);

        mockWebServer.shutdown();
    }

    /**
     * Can also be used to inject spy methods.
     */
    @SuppressWarnings("WeakerAccess")
    protected JsonOkHttpRequest<D> spyForNormal(JsonOkHttpRequest<D> request) {
        return spy(request);
    }
}
