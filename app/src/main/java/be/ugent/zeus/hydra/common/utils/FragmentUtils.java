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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.IdRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;

import java.util.function.Function;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.BaseActivity;
import be.ugent.zeus.hydra.common.ui.RefreshViewModel;
import org.jetbrains.annotations.NotNull;

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

    // TODO: this is an experimental abstraction, since there isn't a lot of benefit vs just doing it
    public static void registerMenuProvider(@NonNull Fragment fragment, @MenuRes int menuRes, @IdRes int[] icons, Function<MenuItem, Boolean> onSelected) {
        BaseActivity<?> activity = requireBaseActivity(fragment);

        activity.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater menuInflater) {
                menuInflater.inflate(menuRes, menu);
                activity.tintToolbarIcons(menu, icons);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull @NotNull MenuItem menuItem) {
                return onSelected.apply(menuItem);
            }
        }, fragment.getViewLifecycleOwner());
    }

    public static void registerRefreshMenu(@NonNull Fragment fragment, @NonNull RefreshViewModel viewModel) {
        registerMenuProvider(fragment, R.menu.menu_resto, new int[]{R.id.action_refresh}, menuItem -> {
            if (menuItem.getItemId() == R.id.action_refresh) {
                viewModel.onRefresh();
                return true;
            }
            return false;
        });
    }
}
