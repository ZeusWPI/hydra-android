package be.ugent.zeus.hydra.minerva.common;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * This dispatcher will return 200 when authenticated, but 403 when not authenticated, and 503 when authenticated
 * with a wrong or expired key (just like Minerva).
 */
public class AuthDispatcher extends Dispatcher {

    private final String token;
    private final MockResponse correctResponse;

    public AuthDispatcher(String token, MockResponse correctResponse) {
        this.token = "Bearer " + token;
        this.correctResponse = correctResponse;
    }

    @Override
    public MockResponse dispatch(RecordedRequest request) {
        String headerToken = request.getHeader("Authorization");
        if (headerToken == null) {
            return new MockResponse().setResponseCode(403);
        } else if (headerToken.equals(token)) {
            return correctResponse;
        } else {
            return new MockResponse().setResponseCode(503);
        }
    }
}
