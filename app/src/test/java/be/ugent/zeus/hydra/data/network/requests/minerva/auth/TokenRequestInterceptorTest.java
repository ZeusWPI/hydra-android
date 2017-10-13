package be.ugent.zeus.hydra.data.network.requests.minerva.auth;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.DataInputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class TokenRequestInterceptorTest {

    private static final String TEST_TOKEN = "test_token";

    @Test
    public void test() throws IOException {

        byte[] expectedBody = new byte[] {0, 1, 0, 1};

        TokenRequestInterceptor interceptor = new TokenRequestInterceptor(TEST_TOKEN);

        MockClientHttpRequest request = new MockClientHttpRequest();
        ClientHttpRequestExecution execution = (r, body) -> new MockClientHttpResponse(body, HttpStatus.OK);

        ClientHttpResponse response = interceptor.intercept(request, expectedBody, execution);

        // Assert the body.
        byte[] receivedData = new byte[expectedBody.length];
        DataInputStream dataIs = new DataInputStream(response.getBody());
        dataIs.readFully(receivedData);
        assertArrayEquals(expectedBody, receivedData);

        // Assert the headers.
        HttpHeaders adjusted = request.getHeaders();
        assertEquals("Bearer " + TEST_TOKEN, adjusted.getAuthorization());
        assertEquals(TEST_TOKEN, adjusted.getFirst("X-Bearer-Token"));
    }
}