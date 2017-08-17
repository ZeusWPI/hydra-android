package be.ugent.zeus.hydra.data.network.requests;

import be.ugent.zeus.hydra.data.network.Endpoints;
import be.ugent.zeus.hydra.repository.requests.Result;
import be.ugent.zeus.hydra.utils.StringUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
public class UrgentUrlRequestTest {

    @Test
    public void testNormal() throws IOException {

        Resource resource = new ClassPathResource("listen_live.config");
        HttpURLConnection connection = mock(HttpURLConnection.class);
        when(connection.getInputStream()).thenReturn(resource.getInputStream());

        URL url = new URL(null, "http://www.example.com", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) {
                return connection;
            }
        });

        TestUrgentRequest request = new TestUrgentRequest(url);
        Result<String> result = request.performRequest(null);

        assertTrue(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.isWithoutError());

        String expected = StringUtils.convertStreamToString(resource.getInputStream());
        assertEquals(expected, result.getData());
    }

    @Test
    public void testError() throws MalformedURLException {

        URL url = new URL(null, "http://www.example.com", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) throws IOException {
                throw new IOException("Hello");
            }
        });

        TestUrgentRequest request = new TestUrgentRequest(url);

        Result<?> result = request.performRequest(null);

        assertFalse(result.hasData());
        assertTrue(result.isDone());
        assertTrue(result.hasException());
    }

    @Test
    public void testURL() throws MalformedURLException {
        UrgentUrlRequest request = new UrgentUrlRequest();
        assertEquals(new URL(Endpoints.URGENT_CONFIG_URL), request.getURL());
    }

    public static class TestUrgentRequest extends UrgentUrlRequest {

        private final URL url;

        public TestUrgentRequest(URL url) {
            this.url = url;
        }

        @Override
        protected URL getURL() throws MalformedURLException {
            return url;
        }
    }
}