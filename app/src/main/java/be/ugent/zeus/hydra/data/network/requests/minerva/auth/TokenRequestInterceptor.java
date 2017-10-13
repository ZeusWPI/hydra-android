/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University Ghent
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in all copies or
 *      substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package be.ugent.zeus.hydra.data.network.requests.minerva.auth;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Intercepts the HTTP Requests and injects the access token in header of the request. The token is set in two headers:
 * - Authorization
 * - X-Bearer-Token
 *
 * @author kevin
 * @author Niko Strijbol
 */
public class TokenRequestInterceptor implements ClientHttpRequestInterceptor {

    private final String token;

    public TokenRequestInterceptor(String token) {
        this.token = token;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        request.getHeaders().setAuthorization(new BearerAuthentication(token));
        request.getHeaders().set("X-Bearer-Token", token);

        // Log API Request
        //Log.d(TAG, String.format("API Request: %s", request.getURI().toString()));

        // Perform CacheRequest
        return execution.execute(request, body);
    }

    private static class BearerAuthentication extends HttpAuthentication {

        private final String token;

        private BearerAuthentication(String token) {
            this.token = token;
        }

        @Override
        public String getHeaderValue() {
            return String.format("Bearer %s", token);
        }
    }
}