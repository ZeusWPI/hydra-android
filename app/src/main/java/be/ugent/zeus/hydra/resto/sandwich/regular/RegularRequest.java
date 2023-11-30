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

package be.ugent.zeus.hydra.resto.sandwich.regular;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.JsonArrayRequest;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * CacheRequest the list of sandwiches.
 *
 * @author feliciaan
 */
class RegularRequest extends JsonArrayRequest<RegularSandwich> {

    RegularRequest(Context context) {
        super(context, RegularSandwich.class);
    }

    @NonNull
    @Override
    public Result<List<RegularSandwich>> execute(@NonNull Bundle args) {
        return super.execute(args).map(sandwiches -> {
            Collections.sort(sandwiches, (o1, o2) -> o1.name().compareToIgnoreCase(o2.name()));
            return sandwiches;
        });
    }

    @NonNull
    @Override
    protected String apiUrl() {
        return Endpoints.ZEUS_V2 + "resto/sandwiches/static.json";
    }

    @Override
    public Duration cacheDuration() {
        return ChronoUnit.WEEKS.getDuration();
    }
}
