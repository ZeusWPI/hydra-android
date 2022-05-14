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

package be.ugent.zeus.hydra.common.converter;

import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.moshi.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class PairJsonAdapter<L, R> extends JsonAdapter<Pair<L, R>> {
    
    private final JsonAdapter<L> leftAdapter;
    private final JsonAdapter<R> rightAdapter;

    public PairJsonAdapter(JsonAdapter<L> leftAdapter, JsonAdapter<R> rightAdapter) {
        this.leftAdapter = leftAdapter;
        this.rightAdapter = rightAdapter;
    }

    @Nullable
    @Override
    public Pair<L, R> fromJson(JsonReader reader) throws IOException {
        if (reader.peek() == JsonReader.Token.NULL) {
            return null;
        }
        reader.beginArray();
        L lValue = leftAdapter.fromJson(reader);
        R rValue = rightAdapter.fromJson(reader);
        reader.endArray();
        return Pair.create(lValue, rValue);
    }

    @Override
    public void toJson(@NonNull JsonWriter writer, @Nullable Pair<L, R> value) throws IOException {
        if (value == null) {
            writer.nullValue();
            return;
        }
        writer.beginArray();
        leftAdapter.toJson(writer, value.first);
        rightAdapter.toJson(writer, value.second);
        writer.endArray();
    }
    
    public static class Factory implements JsonAdapter.Factory {

        @Nullable
        @Override
        public JsonAdapter<?> create(@NonNull Type type, @NonNull Set<? extends Annotation> annotations, @NonNull Moshi moshi) {
            if (!annotations.isEmpty()) {
                return null; // Annotations? This factory doesn't apply.
            }

            if (!(type instanceof ParameterizedType)) {
                return null; // No type parameter? This factory doesn't apply.
            }

            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getRawType() != Pair.class) {
                return null; // Not a pair? This factory doesn't apply.
            }

            Type leftType = parameterizedType.getActualTypeArguments()[0];
            Type rightType = parameterizedType.getActualTypeArguments()[1];

            JsonAdapter<?> leftAdapter = moshi.adapter(leftType);
            JsonAdapter<?> rightAdapter = moshi.adapter(rightType);
            
            return new PairJsonAdapter<>(leftAdapter, rightAdapter).nullSafe();
        }
    }
}
