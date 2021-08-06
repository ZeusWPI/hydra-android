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

package be.ugent.zeus.hydra.common.request;

/**
 * Exception thrown by a {@link Request} when something goes wrong while producing the data.
 *
 * @author Niko Strijbol
 */
public class RequestException extends Exception {

    /**
     * No further details are known. You probably want one of the other constructors.
     *
     * @see Exception#Exception()
     */
    public RequestException() {
        super();
    }

    /**
     * @see Exception#Exception(Throwable)
     */
    public RequestException(Throwable cause) {
        super(cause);
    }

    /**
     * @see Exception#Exception(String, Throwable)
     */
    public RequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see Exception#Exception(String)
     */
    public RequestException(String message) {
        super(message);
    }
}