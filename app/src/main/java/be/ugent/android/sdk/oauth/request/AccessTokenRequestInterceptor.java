/**
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

package be.ugent.android.sdk.oauth.request;

import android.util.Log;
import be.ugent.android.sdk.oauth.AuthorizationManager;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Intercepts the HTTP Requests and injects the access token in the Uri.
 *
 * @author kevin
 */
public class AccessTokenRequestInterceptor implements ClientHttpRequestInterceptor {

    private final String TAG = "AccessTokenRequestInter";

    private final AuthorizationManager authorizationManager;

    public AccessTokenRequestInterceptor(AuthorizationManager manager) {
        authorizationManager = manager;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (authorizationManager.isAuthenticated()) {
            String accessToken = authorizationManager.getAccessToken();
            if (accessToken != null) {
                request.getHeaders().set("Authorization", String.format("Bearer %s", accessToken));
                request.getHeaders().set("X-Bearer-Token", accessToken);

                // Log API Request
                Log.i(TAG, String.format("API Request: %s", request.getURI().toString()));
            }
        }

        // Perform Request
        ClientHttpResponse response = execution.execute(request, body);

        return response;
    }

}
