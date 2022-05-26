/*
 * Copyright (c) 2022 Niko Strijbol
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

package be.ugent.zeus.hydra.wpi.door;

/**
 * Result of a successful door request.
 * 
 * @author Niko Strijbol
 * @see <a href="https://github.com/ZeusWPI/mattermore/blob/5576da45f4cf7af2e206e15907885b30151cd42b/app/app.py#L202">Door API source code</a>
 */
public class DoorRequestResult {
    private String status;
    private String before;
    
    public DoorRequestResult() {
        // Moshi
    }

    /**
     * @return This will always be "OK", otherwise there will be no JSON response.
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return The state of the door before the request.
     */
    public String getBefore() {
        return before;
    }
}
