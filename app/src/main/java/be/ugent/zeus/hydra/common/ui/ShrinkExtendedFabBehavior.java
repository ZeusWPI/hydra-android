/*
 * Copyright (C) 2017 The Android Open Source Project
 * Copyright (C) 2022 Niko Strijbol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.ugent.zeus.hydra.common.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.behavior.HideBottomViewOnScrollBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

/**
 * Shrink the extended FAB when scrolling down and extend it when scrolling up.
 * Based on {@link HideBottomViewOnScrollBehavior}.
 * 
 * @author Niko Strijbol
 */
public class ShrinkExtendedFabBehavior extends CoordinatorLayout.Behavior<ExtendedFloatingActionButton> {

    private static final int STATE_SHRUNK = 1;
    private static final int STATE_EXTENDED = 2;

    private int currentState = STATE_EXTENDED;

    public ShrinkExtendedFabBehavior() {
    }

    public ShrinkExtendedFabBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean isScrolledDown() {
        return currentState == STATE_SHRUNK;
    }

    private boolean isScrolledUp() {
        return currentState == STATE_EXTENDED;
    }

    @Override
    public boolean onStartNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull ExtendedFloatingActionButton child,
            @NonNull View directTargetChild,
            @NonNull View target,
            int nestedScrollAxes,
            int type) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(
            @NonNull CoordinatorLayout coordinatorLayout,
            @NonNull ExtendedFloatingActionButton child,
            @NonNull View target,
            int dxConsumed,
            int dyConsumed,
            int dxUnconsumed,
            int dyUnconsumed,
            int type,
            @NonNull int[] consumed) {
        if (dyConsumed > 0) {
            shrink(child);
        } else if (dyConsumed < 0) {
            extend(child);
        }
    }
    
    public void extend(@NonNull ExtendedFloatingActionButton child) {
        if (isScrolledUp()) {
            return;
        }
        currentState = STATE_EXTENDED;
        child.extend();
    }

    public void shrink(@NonNull ExtendedFloatingActionButton child) {
        if (isScrolledDown()) {
            return;
        }
        currentState = STATE_SHRUNK;
        child.shrink();
    }
}
