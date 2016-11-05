/*
 * Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package be.ugent.zeus.hydra.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Partial copy of the {@link java.util.Objects} class from the Java 7 SDK. This is an exact copy, where some methods
 * were removed because they target 1.8 or need access to private code.
 *
 * When we target API 19 and higher, we can switch to the built-in class.
 */
public class Objects {

    private Objects() {
        throw new AssertionError("No java.util.Objects instances for you!");
    }

    /**
     * Returns {@code true} if the arguments are equal to each other and {@code false} otherwise.
     *
     * Consequently, if both arguments are {@code null}, {@code true} is returned and if exactly one argument
     * is {@code null}, {@code false} is returned.  Otherwise, equality is determined by using the
     * {@link Object#equals equals} method of the first argument.
     *
     * @param a an object
     * @param b an object to be compared with {@code a} for equality
     *
     * @return {@code true} if the arguments are equal to each other and {@code false} otherwise
     *
     * @see Object#equals(Object)
     */
    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    /**
     * Returns the hash code of a non-{@code null} argument and 0 for a {@code null} argument.
     *
     * @param o an object
     *
     * @return the hash code of a non-{@code null} argument and 0 for a {@code null} argument
     *
     * @see Object#hashCode
     */
    public static int hashCode(Object o) {
        return o != null ? o.hashCode() : 0;
    }

    /**
     * Generates a hash code for a sequence of input values. The hash code is generated as if all the input values
     * were placed into an array, and that array were hashed by calling {@link Arrays#hashCode(Object[])}.
     *
     * This method is useful for implementing {@link Object#hashCode()} on objects containing multiple fields. For
     * example, if an object that has three fields, {@code x}, {@code y}, and {@code z}, one could write:
     *
     * <blockquote><pre>
     * &#064;Override public int hashCode() {
     *     return Objects.hash(x, y, z);
     * }
     * </pre></blockquote>
     *
     * <b>Warning: When a single object reference is supplied, the returned value does not equal the hash code of
     * that object reference.</b> This value can be computed by calling {@link #hashCode(Object)}.
     *
     * @param values the values to be hashed
     *
     * @return a hash value of the sequence of input values
     *
     * @see Arrays#hashCode(Object[])
     * @see List#hashCode
     */
    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    /**
     * Returns the result of calling {@code toString} for a non-{@code null} argument and {@code "null"}
     * for a {@code null} argument.
     *
     * @param o an object
     *
     * @return the result of calling {@code toString} for a non-{@code null} argument and {@code "null"} for a {@code
     * null} argument
     *
     * @see Object#toString
     * @see String#valueOf(Object)
     */
    public static String toString(Object o) {
        return String.valueOf(o);
    }

    /**
     * Returns the result of calling {@code toString} on the first argument if the first argument is not {@code null}
     * and returns the second argument otherwise.
     *
     * @param o           an object
     * @param nullDefault string to return if the first argument is {@code null}
     *
     * @return the result of calling {@code toString} on the first argument if it is not {@code null} and the second
     * argument otherwise.
     *
     * @see Objects#toString(Object)
     */
    public static String toString(Object o, String nullDefault) {
        return (o != null) ? o.toString() : nullDefault;
    }

    /**
     * Returns 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
     * Consequently, if both arguments are {@code null} 0 is returned.
     *
     * Note that if one of the arguments is {@code null}, a {@code NullPointerException} may or may not be thrown
     * depending on what ordering policy, if any, the {@link Comparator Comparator} chooses to have for {@code null}
     * values.
     *
     * @param <T> the type of the objects being compared
     * @param a   an object
     * @param b   an object to be compared with {@code a}
     * @param c   the {@code Comparator} to compare the first two arguments
     *
     * @return 0 if the arguments are identical and {@code c.compare(a, b)} otherwise.
     *
     * @see Comparable
     * @see Comparator
     */
    public static <T> int compare(T a, T b, Comparator<? super T> c) {
        return (a == b) ? 0 : c.compare(a, b);
    }

    /**
     * Checks that the specified object reference is not {@code null}. This method is designed primarily for doing
     * parameter validation in methods and constructors, as demonstrated below:
     *
     * <blockquote><pre>
     * public Foo(Bar bar) {
     *     this.bar = Objects.requireNonNull(bar);
     * }
     * </pre></blockquote>
     *
     * @param obj the object reference to check for nullity
     * @param <T> the type of the reference
     *
     * @return {@code obj} if not {@code null}
     *
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    public static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }

    /**
     * Checks that the specified object reference is not {@code null} and throws a customized {@link NullPointerException}
     * if it is. This method is designed primarily for doing parameter validation in methods and constructors with
     * multiple parameters, as demonstrated below:
     *
     * <blockquote><pre>
     * public Foo(Bar bar, Baz baz) {
     *     this.bar = Objects.requireNonNull(bar, "bar must not be null");
     *     this.baz = Objects.requireNonNull(baz, "baz must not be null");
     * }
     * </pre></blockquote>
     *
     * @param obj     the object reference to check for nullity
     * @param message detail message to be used in the event that a {@code NullPointerException} is thrown
     * @param <T>     the type of the reference
     *
     * @return {@code obj} if not {@code null}
     *
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }
}