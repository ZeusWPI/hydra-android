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
package be.ugent.zeus.hydra.auth;

/**
 * Wrapper class for the OAuth configuration.
 *
 * @author Niko Strijbol
 * @author kevin
 */
public class OAuthConfiguration {

    public final String API_KEY;
    public final String API_SECRET;
    public final String CALLBACK_URI;

    private OAuthConfiguration(Builder builder) {
        this.API_KEY = builder.apiKey;
        this.API_SECRET = builder.apiSecret;
        this.CALLBACK_URI = builder.callbackUri;
    }

    public static class Builder {
        private String callbackUri;
        private String apiSecret;
        private String apiKey;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder apiSecret(String apiSecret) {
            this.apiSecret = apiSecret;
            return this;
        }

        public Builder callbackUri(String callbackUri) {
            this.callbackUri = callbackUri;
            return this;
        }

        public OAuthConfiguration build() {
            if (apiKey == null) {
                throw new IllegalStateException("Api Key required.");
            }
            if (apiSecret == null) {
                throw new IllegalStateException("Api Secret required.");
            }
            if (callbackUri == null) {
                throw new IllegalStateException("Callback Uri required");
            }
            return new OAuthConfiguration(this);
        }
    }
}