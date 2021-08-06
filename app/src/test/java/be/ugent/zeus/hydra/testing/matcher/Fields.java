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

package be.ugent.zeus.hydra.testing.matcher;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * Takes care of selecting which fields need to be considered.
 *
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.N)
class Fields<T> {

    private final Class<T> clazz;

    private final Collection<String> ignoredFields;
    private List<Field> results;

    Fields(Class<T> clazz) {
        this.clazz = clazz;
        this.ignoredFields = new HashSet<>();
    }

    public void ignoreFields(String... fields) {
        Collections.addAll(this.ignoredFields, fields);
    }

    public List<Field> getFields() {
        return FieldUtils.getAllFieldsList(clazz).stream()
                .filter(f -> !Modifier.isTransient(f.getModifiers()))
                .filter(f -> !ignoredFields.contains(f.getName()))
                .collect(Collectors.toList());
    }
}