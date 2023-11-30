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

package be.ugent.zeus.hydra.testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.*;
import java.util.*;
import java.util.stream.Stream;

import be.ugent.zeus.hydra.feed.cards.dismissal.DismissalDaoTest;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.api.SingleTypeEqualsVerifierApi;
import okio.BufferedSource;
import okio.Okio;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import static org.jeasy.random.FieldPredicates.named;

/**
 * General utilities and helper methods for use within the tests.
 *
 * @author Niko Strijbol
 */
public class Utils {

    private static final Random random = new Random();

    public static <T> T generate(Class<T> clazz, String... exclude) {
        EasyRandomParameters params = new EasyRandomParameters()
                .scanClasspathForConcreteTypes(true)
                .randomize(ZonedDateTime.class, ZonedDateTime::now)
                .randomize(LocalDate.class, LocalDate::now)
                .randomize(OffsetDateTime.class, OffsetDateTime::now)
                .randomize(Instant.class, Instant::now);
        for (String excluded : exclude) {
            params.excludeField(named(excluded));
        }
        return new EasyRandom(params).nextObject(clazz);
    }

    public static <T> Stream<T> generate(Class<T> clazz, int amount, String... exclude) {
        EasyRandomParameters params = new EasyRandomParameters()
                .scanClasspathForConcreteTypes(true)
                .randomize(ZonedDateTime.class, ZonedDateTime::now)
                .randomize(LocalDate.class, LocalDate::now)
                .randomize(OffsetDateTime.class, OffsetDateTime::now)
                .randomize(Instant.class, Instant::now);
        for (String excluded : exclude) {
            params.excludeField(named(excluded));
        }
        return new EasyRandom(params).objects(clazz, amount);
    }

    /**
     * Default verifier with support for {@link ZonedDateTime}.
     *
     * @param clazz Class to verify.
     * @param <T>   Type of class.
     * @return The verifier.
     */
    public static <T> SingleTypeEqualsVerifierApi<T> defaultVerifier(Class<T> clazz) {
        return EqualsVerifier.forClass(clazz)
                .withPrefabValues(ZonedDateTime.class, ZonedDateTime.now(), ZonedDateTime.now().minusDays(2));
    }

    public static <T> T readJson(Moshi moshi, String file, Class<T> type) throws IOException {
        BufferedSource source = Okio.buffer(Okio.source(new FileInputStream(getResourceFile(file))));
        JsonAdapter<T> adapter = moshi.adapter(type);
        return adapter.fromJson(source);
    }

    public static <T> T readJson(Moshi moshi, String file, Type type) throws IOException {
        BufferedSource source = Okio.buffer(Okio.source(new FileInputStream(getResourceFile(file))));
        JsonAdapter<T> adapter = moshi.adapter(type);
        return adapter.fromJson(source);
    }

    public static File getResourceFile(String resourcePath) {
        //noinspection DataFlowIssue
        return new File(DismissalDaoTest.class.getClassLoader().getResource(resourcePath).getFile());
    }

    public static <T> T getRandom(List<T> collection) {
        return collection.get(random.nextInt(collection.size()));
    }

    public static <T> List<T> getRandom(final List<T> collection, int amount) {
        if (amount > collection.size()) {
            throw new IllegalArgumentException("The number of requested items cannot be larger than the number of total items.");
        }

        List<T> copy = new ArrayList<>(collection);
        Collections.shuffle(copy);
        return copy.subList(0, amount);
    }

    /**
     * Set a field to a value using reflection.
     * <p>
     * Note: while useful when testing json classes for example, try to minimize usage.
     *
     * @param instance The instance to set the field on.
     * @param name     The name of the field to set.
     * @param value    The value to set the field to.
     */
    @Deprecated
    public static void setField(Object instance, String name, Object value) {
        try {

            Field declaredField = instance.getClass().getDeclaredField(name);
            boolean accessible = declaredField.isAccessible();
            declaredField.setAccessible(true);
            declaredField.set(instance, value);
            declaredField.setAccessible(accessible);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
