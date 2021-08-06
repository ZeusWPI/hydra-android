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

package be.ugent.zeus.hydra.library.details;

import android.content.Context;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Request;
import be.ugent.zeus.hydra.library.Library;

/**
 * Get the opening hours for one library.
 *
 * @author Niko Strijbol
 */
public class OpeningHoursRequest extends JsonArrayRequest<OpeningHours> {

    private final String libraryCode;

    public OpeningHoursRequest(Context context, Library library) {
        this(context, library.getCode());
    }

    public OpeningHoursRequest(Context context, String libraryCode) {
        super(context, OpeningHours.class);
        this.libraryCode = libraryCode;
    }

    @Override
    public Duration getCacheDuration() {
        return Duration.ofDays(4);
    }

    @NonNull
    @Override
    protected String getAPIUrl() {
        return Endpoints.LIBRARY + "libraries/" + libraryCode + "/calendar.json";
    }

    public Request<Optional<OpeningHours>> forDay(LocalDate date) {
        return map(openingHours -> openingHours.stream()
                .filter(o -> date.equals(o.getDate()))
                .findFirst());
    }
}