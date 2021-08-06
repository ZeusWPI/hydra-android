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

package be.ugent.zeus.hydra.common.utils;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import be.ugent.zeus.hydra.common.ui.BaseActivity;

/**
 * @author Niko Strijbol
 */
public class FragmentUtils {

    @NonNull
    public static BaseActivity<?> requireBaseActivity(Fragment fragment) {
        Activity activity = fragment.requireActivity();
        if (activity instanceof BaseActivity) {
            return (BaseActivity<?>) activity;
        } else {
            throw new IllegalStateException("This method can only be used if the Fragment is attached to a BaseActivity.");
        }
    }

    @NonNull
    public static Bundle requireArguments(Fragment fragment) {
        Bundle arguments = fragment.getArguments();
        if (arguments == null) {
            throw new IllegalStateException("The Fragment was not properly initialised and misses arguments.");
        } else {
            return arguments;
        }
    }
}
