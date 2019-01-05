/*
 * MIT License
 *
 * Copyright (c) 2017 Jan Heinrich Reimer
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

package com.heinrichreimersoftware.materialintro.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.InterpolatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextSwitcher;

import com.heinrichreimersoftware.materialintro.R;
import com.heinrichreimersoftware.materialintro.slide.ButtonCtaSlide;
import com.heinrichreimersoftware.materialintro.slide.Slide;
import com.heinrichreimersoftware.materialintro.slide.SlideAdapter;
import com.heinrichreimersoftware.materialintro.util.AnimUtils;
import com.heinrichreimersoftware.materialintro.util.CheatSheet;
import com.heinrichreimersoftware.materialintro.view.FadeableViewPager;
import com.heinrichreimersoftware.materialintro.view.InkPageIndicator;
import com.heinrichreimersoftware.materialintro.view.parallax.Parallaxable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressLint("Registered")
public class IntroActivity extends AppCompatActivity implements IntroNavigation {
    private static final String KEY_CURRENT_ITEM =
            "com.heinrichreimersoftware.materialintro.app.IntroActivity.KEY_CURRENT_ITEM";
    private static final String KEY_FULLSCREEN =
            "com.heinrichreimersoftware.materialintro.app.IntroActivity.KEY_FULLSCREEN";
    private static final String KEY_BUTTON_CTA_VISIBLE =
            "com.heinrichreimersoftware.materialintro.app.IntroActivity.KEY_BUTTON_CTA_VISIBLE";

    private boolean activityCreated = false;

    private ConstraintLayout miFrame;
    private FadeableViewPager miPager;
    private InkPageIndicator miPagerIndicator;
    private TextSwitcher miButtonCta;
    private ImageButton miButtonBack;
    private ImageButton miButtonNext;

    //Settings constants
    @IntDef({BUTTON_NEXT_FUNCTION_NEXT, BUTTON_NEXT_FUNCTION_NEXT_FINISH})
    @Retention(RetentionPolicy.SOURCE)
    @interface ButtonNextFunction {
    }

    public static final int BUTTON_NEXT_FUNCTION_NEXT = 1;
    public static final int BUTTON_NEXT_FUNCTION_NEXT_FINISH = 2;

    @IntDef({BUTTON_BACK_FUNCTION_BACK, BUTTON_BACK_FUNCTION_SKIP})
    @Retention(RetentionPolicy.SOURCE)
    @interface ButtonBackFunction {
    }

    public static final int BUTTON_BACK_FUNCTION_BACK = 1;
    public static final int BUTTON_BACK_FUNCTION_SKIP = 2;

    @IntDef({BUTTON_CTA_TINT_MODE_BACKGROUND, BUTTON_CTA_TINT_MODE_TEXT})
    @Retention(RetentionPolicy.SOURCE)
    @interface ButtonCtaTintMode {
    }

    public static final int BUTTON_CTA_TINT_MODE_BACKGROUND = 1;
    public static final int BUTTON_CTA_TINT_MODE_TEXT = 2;

    public static final int DEFAULT_AUTOPLAY_DELAY = 1500;
    public static final int INFINITE = -1;
    public static final int DEFAULT_AUTOPLAY_REPEAT_COUNT = INFINITE;

    public static final Interpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private final ArgbEvaluator evaluator = new ArgbEvaluator();

    private SlideAdapter adapter;

    private IntroPageChangeListener listener = new IntroPageChangeListener();

    private int position = 0;
    private float positionOffset = 0;

    //Settings
    private boolean fullscreen = false;
    private boolean buttonCtaVisible = false;
    @ButtonNextFunction
    private int buttonNextFunction = BUTTON_NEXT_FUNCTION_NEXT_FINISH;
    @ButtonBackFunction
    private int buttonBackFunction = BUTTON_BACK_FUNCTION_SKIP;
    @ButtonCtaTintMode
    private int buttonCtaTintMode = BUTTON_CTA_TINT_MODE_BACKGROUND;
    private NavigationPolicy navigationPolicy = null;
    private List<OnNavigationBlockedListener> navigationBlockedListeners = new ArrayList<>();
    private CharSequence buttonCtaLabel = null;
    @StringRes
    private int buttonCtaLabelRes = 0;
    private View.OnClickListener buttonCtaClickListener = null;

    private Handler autoplayHandler = new Handler();
    private Runnable autoplayCallback = null;
    private int autoplayCounter;
    private long autoplayDelay;

    private Interpolator pageScrollInterpolator;
    private long pageScrollDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageScrollInterpolator = AnimationUtils.loadInterpolator(this, android.R.interpolator.accelerate_decelerate);
        pageScrollDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_CURRENT_ITEM)) {
                position = savedInstanceState.getInt(KEY_CURRENT_ITEM, position);
            }
            if (savedInstanceState.containsKey(KEY_FULLSCREEN)) {
                fullscreen = savedInstanceState.getBoolean(KEY_FULLSCREEN, fullscreen);
            }
            if (savedInstanceState.containsKey(KEY_BUTTON_CTA_VISIBLE)) {
                buttonCtaVisible = savedInstanceState.getBoolean(KEY_BUTTON_CTA_VISIBLE, buttonCtaVisible);
            }
        }

        if (fullscreen) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setSystemUiFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, true);
                updateFullscreen();
            } else {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setContentView(R.layout.mi_activity_intro);
        initViews();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        activityCreated = true;

        updateTaskDescription();
        updateButtonNextDrawable();
        updateButtonBackDrawable();
        updateScrollPositions();
        miFrame.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                updateScrollPositions();
                v.removeOnLayoutChangeListener(this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFullscreen();
    }

    @Override
    public void onUserInteraction() {
        if (isAutoplaying())
            cancelAutoplay();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        updateButtonCta();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_ITEM, miPager.getCurrentItem());
        outState.putBoolean(KEY_FULLSCREEN, fullscreen);
        outState.putBoolean(KEY_BUTTON_CTA_VISIBLE, buttonCtaVisible);
    }

    @Override
    public void onBackPressed() {
        if (position > 0) {
            previousSlide();
            return;
        }
        Intent returnIntent = onSendActivityResult(RESULT_CANCELED);
        if (returnIntent != null)
            setResult(RESULT_CANCELED, returnIntent);
        else
            setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public Intent onSendActivityResult(int result) {
        return null;
    }

    @Override
    protected void onDestroy() {
        if (isAutoplaying()) {
            cancelAutoplay();
        }

        activityCreated = false;
        super.onDestroy();
    }

    private void setSystemUiFlags(int flags, boolean value) {
        int systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        if (value) {
            systemUiVisibility |= flags;
        } else {
            systemUiVisibility &= ~flags;
        }
        getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setFullscreenFlags(boolean fullscreen) {
        int fullscreenFlags = View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            fullscreenFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        setSystemUiFlags(fullscreenFlags, fullscreen);
    }

    private void initViews() {
        // bind views
        miFrame = (ConstraintLayout) findViewById(R.id.mi_frame);
        miPager = (FadeableViewPager) findViewById(R.id.mi_pager);
        miPagerIndicator = (InkPageIndicator) findViewById(R.id.mi_pager_indicator);
        miButtonCta = (TextSwitcher) findViewById(R.id.mi_button_cta);
        miButtonBack = (ImageButton) findViewById(R.id.mi_button_back);
        miButtonNext = (ImageButton) findViewById(R.id.mi_button_next);

        if (miButtonCta != null) {
            miButtonCta.setInAnimation(this, R.anim.mi_fade_in);
            miButtonCta.setOutAnimation(this, R.anim.mi_fade_out);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new SlideAdapter(fragmentManager);

        miPager.setAdapter(adapter);
        miPager.addOnPageChangeListener(listener);
        miPager.setCurrentItem(position, false);

        miPagerIndicator.setViewPager(miPager);

        resetButtonNextOnClickListener();
        resetButtonBackOnClickListener();

        CheatSheet.setup(miButtonNext);
        CheatSheet.setup(miButtonBack);
    }

    public void setButtonNextOnClickListener(View.OnClickListener onClickListener) {
        miButtonNext.setOnClickListener(onClickListener);
    }

    public void setButtonBackOnClickListener(View.OnClickListener onClickListener) {
        miButtonBack.setOnClickListener(onClickListener);
    }

    public void resetButtonNextOnClickListener() {
        miButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextSlide();
            }
        });
    }

    public void resetButtonBackOnClickListener() {
        miButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performButtonBackPress();
            }
        });
    }

    private void smoothScrollPagerTo(final int position) {
        if (miPager.isFakeDragging())
            return;

        ValueAnimator animator = ValueAnimator.ofFloat(miPager.getCurrentItem(), position);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (miPager.isFakeDragging())
                    miPager.endFakeDrag();
                miPager.setCurrentItem(position);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (miPager.isFakeDragging())
                    miPager.endFakeDrag();
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float position = (Float) animation.getAnimatedValue();

                fakeDragToPosition(position);
            }

            private boolean fakeDragToPosition(float position) {
                // The following mimics the underlying calculations in ViewPager
                float scrollX = miPager.getScrollX();
                int pagerWidth = miPager.getWidth();
                int currentPosition = miPager.getCurrentItem();

                if (position > currentPosition && Math.floor(position) != currentPosition && position % 1 != 0) {
                    miPager.setCurrentItem((int) Math.floor(position), false);
                } else if (position < currentPosition && Math.ceil(position) != currentPosition && position % 1 != 0) {
                    miPager.setCurrentItem((int) Math.ceil(position), false);
                }

                if (!miPager.isFakeDragging() && !miPager.beginFakeDrag())
                    return false;

                miPager.fakeDragBy(scrollX - pagerWidth * position);
                return true;
            }
        });

        int distance = Math.abs(position - miPager.getCurrentItem());

        animator.setInterpolator(pageScrollInterpolator);
        animator.setDuration(calculateScrollDuration(distance));
        animator.start();
    }

    private long calculateScrollDuration(int distance) {
        return Math.round(pageScrollDuration * (distance + Math.sqrt(distance)) / 2);
    }

    @Override
    public boolean goToSlide(int position) {
        int lastPosition = miPager.getCurrentItem();

        if (lastPosition >= adapter.getCount()) {
            finishIfNeeded();
        }

        int newPosition = lastPosition;

        position = Math.max(0, Math.min(position, getCount()));

        if (position > lastPosition) {
            // Go forward
            while (newPosition < position && canGoForward(newPosition, true)) {
                newPosition++;
            }
        } else if (position < lastPosition) {
            // Go backward
            while (newPosition > position && canGoBackward(newPosition, true)) {
                newPosition--;
            }
        } else {
            // Noting to do here
            return true;
        }

        boolean blocked = false;
        if (newPosition != position) {
            // Could not go the complete way to the given position.
            blocked = true;

            if (position > lastPosition) {
                AnimUtils.applyShakeAnimation(this, miButtonNext);
            } else if (position < lastPosition) {
                AnimUtils.applyShakeAnimation(this, miButtonBack);
            }
        }

        // Scroll to new position
        smoothScrollPagerTo(newPosition);

        return !blocked;
    }

    @Override
    public boolean nextSlide() {
        int currentItem = miPager.getCurrentItem();
        return goToSlide(currentItem + 1);
    }

    private int nextSlideAuto() {
        int lastPosition = miPager.getCurrentItem();
        int count = getCount();

        if (count == 1) {
            return 0;
        } else if (miPager.getCurrentItem() >= count - 1) {
            while (lastPosition >= 0 && canGoBackward(lastPosition, true)) {
                lastPosition--;
            }
            if (autoplayCounter > 0)
                autoplayCounter--;
        } else if (canGoForward(lastPosition, true)) {
            lastPosition++;
        }

        int distance = Math.abs(lastPosition - miPager.getCurrentItem());

        if (lastPosition == miPager.getCurrentItem())
            return 0;

        smoothScrollPagerTo(lastPosition);

        if (autoplayCounter == 0)
            return 0;
        return distance;

    }

    @Override
    public boolean previousSlide() {
        int currentItem = miPager.getCurrentItem();
        return goToSlide(currentItem - 1);
    }

    @Override
    public boolean goToLastSlide() {
        return goToSlide(getCount() - 1);
    }

    @Override
    public boolean goToFirstSlide() {
        return goToSlide(0);
    }

    private void performButtonBackPress() {
        if (buttonBackFunction == BUTTON_BACK_FUNCTION_SKIP) {
            goToSlide(getCount());
        } else if (buttonBackFunction == BUTTON_BACK_FUNCTION_BACK) {
            previousSlide();
        }
    }

    private boolean canGoForward(int position, boolean notifyListeners) {
        if (position >= getCount()) {
            return false;
        }
        if (position < 0) {
            return true;
        }

        if (buttonNextFunction == BUTTON_NEXT_FUNCTION_NEXT && position >= getCount() - 1)
            //Block finishing when button "next" function is not "finish".
            return false;

        boolean canGoForward = (navigationPolicy == null || navigationPolicy.canGoForward(position)) &&
                getSlide(position).canGoForward();
        if (!canGoForward && notifyListeners) {
            for (OnNavigationBlockedListener listener : navigationBlockedListeners) {
                listener.onNavigationBlocked(position, OnNavigationBlockedListener.DIRECTION_FORWARD);
            }
        }
        return canGoForward;
    }

    private boolean canGoBackward(int position, boolean notifyListeners) {
        if (position <= 0) {
            return false;
        }
        if (position >= getCount()) {
            return true;
        }

        boolean canGoBackward = (navigationPolicy == null || navigationPolicy.canGoBackward(position)) &&
                getSlide(position).canGoBackward();
        if (!canGoBackward && notifyListeners) {
            for (OnNavigationBlockedListener listener : navigationBlockedListeners) {
                listener.onNavigationBlocked(position, OnNavigationBlockedListener.DIRECTION_BACKWARD);
            }
        }
        return canGoBackward;
    }


    private boolean finishIfNeeded() {
        if (positionOffset == 0 && position == adapter.getCount()) {
            Intent returnIntent = onSendActivityResult(RESULT_OK);
            if (returnIntent != null)
                setResult(RESULT_OK, returnIntent);
            else
                setResult(RESULT_OK);
            finish();
            overridePendingTransition(0, 0);
            return true;
        }
        return false;
    }

    @Nullable
    private Pair<CharSequence, ? extends View.OnClickListener> getButtonCta(int position) {
        if (position < getCount() && getSlide(position) instanceof ButtonCtaSlide) {
            ButtonCtaSlide slide = (ButtonCtaSlide) getSlide(position);
            if (slide.getButtonCtaClickListener() != null &&
                    (slide.getButtonCtaLabel() != null || slide.getButtonCtaLabelRes() != 0)) {
                if (slide.getButtonCtaLabel() != null) {
                    return Pair.create(slide.getButtonCtaLabel(),
                            slide.getButtonCtaClickListener());
                } else {
                    return Pair.create((CharSequence) getString(slide.getButtonCtaLabelRes()),
                            slide.getButtonCtaClickListener());
                }
            }
        }
        if (buttonCtaVisible) {
            if (buttonCtaLabelRes != 0) {
                return Pair.create((CharSequence) getString(buttonCtaLabelRes),
                        new ButtonCtaClickListener());
            }
            if (!TextUtils.isEmpty(buttonCtaLabel)) {
                return Pair.create(buttonCtaLabel, new ButtonCtaClickListener());
            } else {
                return Pair.create((CharSequence) getString(R.string.mi_label_button_cta),
                        new ButtonCtaClickListener());
            }
        }
        return null;
    }

    private void updateTaskDescription() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String title = getTitle().toString();
            Drawable iconDrawable = getApplicationInfo().loadIcon(getPackageManager());
            Bitmap icon = iconDrawable instanceof BitmapDrawable ? ((BitmapDrawable) iconDrawable).getBitmap() : null;
            int colorPrimary;
            if (position < getCount()) {
                try {
                    colorPrimary = ContextCompat.getColor(IntroActivity.this, getBackgroundDark(position));
                } catch (Resources.NotFoundException e) {
                    colorPrimary = ContextCompat.getColor(IntroActivity.this, getBackground(position));
                }
            } else {
                TypedValue typedValue = new TypedValue();
                TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
                colorPrimary = a.getColor(0, 0);
                a.recycle();
            }
            colorPrimary = ColorUtils.setAlphaComponent(colorPrimary, 0xFF);

            setTaskDescription(new ActivityManager.TaskDescription(title, icon, colorPrimary));
        }
    }

    private void updateBackground() {
        @ColorInt
        int background;
        @ColorInt
        int backgroundNext;
        @ColorInt
        int backgroundDark;
        @ColorInt
        int backgroundDarkNext;

        if (position == getCount()) {
            background = Color.TRANSPARENT;
            backgroundNext = Color.TRANSPARENT;
            backgroundDark = Color.TRANSPARENT;
            backgroundDarkNext = Color.TRANSPARENT;
        } else {
            background = ContextCompat.getColor(IntroActivity.this,
                    getBackground(position));
            backgroundNext = ContextCompat.getColor(IntroActivity.this,
                    getBackground(Math.min(position + 1, getCount() - 1)));

            background = ColorUtils.setAlphaComponent(background, 0xFF);
            backgroundNext = ColorUtils.setAlphaComponent(backgroundNext, 0xFF);

            try {
                backgroundDark = ContextCompat.getColor(IntroActivity.this,
                        getBackgroundDark(position));
            } catch (Resources.NotFoundException e) {
                backgroundDark = ContextCompat.getColor(IntroActivity.this,
                        R.color.mi_status_bar_background);
            }
            try {
                backgroundDarkNext = ContextCompat.getColor(IntroActivity.this,
                        getBackgroundDark(Math.min(position + 1, getCount() - 1)));
            } catch (Resources.NotFoundException e) {
                backgroundDarkNext = ContextCompat.getColor(IntroActivity.this,
                        R.color.mi_status_bar_background);
            }
        }

        if (position + positionOffset >= adapter.getCount() - 1) {
            backgroundNext = ColorUtils.setAlphaComponent(background, 0x00);
            backgroundDarkNext = ColorUtils.setAlphaComponent(backgroundDark, 0x00);
        }

        background = (Integer) evaluator.evaluate(positionOffset, background, backgroundNext);
        backgroundDark = (Integer) evaluator.evaluate(positionOffset, backgroundDark, backgroundDarkNext);

        miFrame.setBackgroundColor(background);

        float[] backgroundDarkHsv = new float[3];
        Color.colorToHSV(backgroundDark, backgroundDarkHsv);
        //Slightly darken the background color a bit for more contrast
        backgroundDarkHsv[2] *= 0.95;
        int backgroundDarker = Color.HSVToColor(backgroundDarkHsv);
        miPagerIndicator.setPageIndicatorColor(backgroundDarker);
        ViewCompat.setBackgroundTintList(miButtonNext, ColorStateList.valueOf(backgroundDarker));
        ViewCompat.setBackgroundTintList(miButtonBack, ColorStateList.valueOf(backgroundDarker));

        @ColorInt
        int backgroundButtonCta = buttonCtaTintMode == BUTTON_CTA_TINT_MODE_TEXT ?
                ContextCompat.getColor(this, android.R.color.white) : backgroundDarker;
        ViewCompat.setBackgroundTintList(miButtonCta.getChildAt(0), ColorStateList.valueOf(backgroundButtonCta));
        ViewCompat.setBackgroundTintList(miButtonCta.getChildAt(1), ColorStateList.valueOf(backgroundButtonCta));

        int iconColor;
        if (ColorUtils.calculateLuminance(backgroundDark) > 0.4) {
            //Light background
            iconColor = ContextCompat.getColor(this, R.color.mi_icon_color_light);
        } else {
            //Dark background
            iconColor = ContextCompat.getColor(this, R.color.mi_icon_color_dark);
        }
        miPagerIndicator.setCurrentPageIndicatorColor(iconColor);
        DrawableCompat.setTint(miButtonNext.getDrawable(), iconColor);
        DrawableCompat.setTint(miButtonBack.getDrawable(), iconColor);

        @ColorInt
        int textColorButtonCta = buttonCtaTintMode == BUTTON_CTA_TINT_MODE_TEXT ?
                backgroundDarker : iconColor;
        ((Button) miButtonCta.getChildAt(0)).setTextColor(textColorButtonCta);
        ((Button) miButtonCta.getChildAt(1)).setTextColor(textColorButtonCta);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(backgroundDark);

            if (position == adapter.getCount()) {
                getWindow().setNavigationBarColor(Color.TRANSPARENT);
            } else if (position + positionOffset >= adapter.getCount() - 1) {
                TypedValue typedValue = new TypedValue();
                TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.navigationBarColor});

                int defaultNavigationBarColor = a.getColor(0, Color.BLACK);

                a.recycle();

                int navigationBarColor = (Integer) evaluator.evaluate(positionOffset, defaultNavigationBarColor, Color.TRANSPARENT);
                getWindow().setNavigationBarColor(navigationBarColor);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int systemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                int flagLightStatusBar = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                if (ColorUtils.calculateLuminance(backgroundDark) > 0.4) {
                    //Light background
                    systemUiVisibility |= flagLightStatusBar;
                } else {
                    //Dark background
                    systemUiVisibility &= ~flagLightStatusBar;
                }
                getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
            }
        }
    }

    private void updateButtonCta() {
        float realPosition = position + positionOffset;
        float yOffset = getResources().getDimensionPixelSize(R.dimen.mi_y_offset);

        if (realPosition < adapter.getCount()) {
            //Before fade
            Pair<CharSequence, ? extends View.OnClickListener> button = getButtonCta(position);
            Pair<CharSequence, ? extends View.OnClickListener> buttonNext = positionOffset == 0 ? null : getButtonCta(position + 1);

            if (button == null) {
                if (buttonNext == null) {
                    //Hide button
                    miButtonCta.setVisibility(View.GONE);
                } else {
                    miButtonCta.setVisibility(View.VISIBLE);
                    //Fade in
                    if (!((Button) miButtonCta.getCurrentView()).getText().equals(buttonNext.first))
                        miButtonCta.setText(buttonNext.first);
                    miButtonCta.getChildAt(0).setOnClickListener(buttonNext.second);
                    miButtonCta.getChildAt(1).setOnClickListener(buttonNext.second);
                    miButtonCta.setAlpha(positionOffset);
                    miButtonCta.setScaleX(positionOffset);
                    miButtonCta.setScaleY(positionOffset);
                    ViewGroup.LayoutParams layoutParams = miButtonCta.getLayoutParams();
                    layoutParams.height = Math.round(getResources().getDimensionPixelSize(R.dimen.mi_button_cta_height) * ACCELERATE_DECELERATE_INTERPOLATOR.getInterpolation(positionOffset));
                    miButtonCta.setLayoutParams(layoutParams);
                }
            } else {
                if (buttonNext == null) {
                    miButtonCta.setVisibility(View.VISIBLE);
                    //Fade out
                    if (!((Button) miButtonCta.getCurrentView()).getText().equals(button.first))
                        miButtonCta.setText(button.first);
                    miButtonCta.getChildAt(0).setOnClickListener(button.second);
                    miButtonCta.getChildAt(1).setOnClickListener(button.second);
                    miButtonCta.setAlpha(1 - positionOffset);
                    miButtonCta.setScaleX(1 - positionOffset);
                    miButtonCta.setScaleY(1 - positionOffset);
                    ViewGroup.LayoutParams layoutParams = miButtonCta.getLayoutParams();
                    layoutParams.height = Math.round(getResources().getDimensionPixelSize(R.dimen.mi_button_cta_height) * ACCELERATE_DECELERATE_INTERPOLATOR.getInterpolation(1 - positionOffset));
                    miButtonCta.setLayoutParams(layoutParams);
                } else {
                    miButtonCta.setVisibility(View.VISIBLE);
                    ViewGroup.LayoutParams layoutParams = miButtonCta.getLayoutParams();
                    layoutParams.height = getResources().getDimensionPixelSize(R.dimen.mi_button_cta_height);
                    miButtonCta.setLayoutParams(layoutParams);
                    //Fade text
                    if (positionOffset >= 0.5f) {
                        if (!((Button) miButtonCta.getCurrentView()).getText().equals(buttonNext.first))
                            miButtonCta.setText(buttonNext.first);
                        miButtonCta.getChildAt(0).setOnClickListener(buttonNext.second);
                        miButtonCta.getChildAt(1).setOnClickListener(buttonNext.second);
                    } else {
                        if (!((Button) miButtonCta.getCurrentView()).getText().equals(button.first))
                            miButtonCta.setText(button.first);
                        miButtonCta.getChildAt(0).setOnClickListener(button.second);
                        miButtonCta.getChildAt(1).setOnClickListener(button.second);
                    }
                }
            }
        }

        if (realPosition < adapter.getCount() - 1) {
            //Reset
            miButtonCta.setTranslationY(0);
        } else {
            //Hide CTA button
            miButtonCta.setTranslationY(positionOffset * yOffset);
        }
    }

    private void updateButtonBackPosition() {
        float realPosition = position + positionOffset;
        float yOffset = getResources().getDimensionPixelSize(R.dimen.mi_y_offset);

        if (realPosition < 1 && buttonBackFunction == BUTTON_BACK_FUNCTION_BACK) {
            //Hide back button
            miButtonBack.setTranslationY((1 - positionOffset) * yOffset);
        } else if (realPosition < adapter.getCount() - 2) {
            //Reset
            miButtonBack.setTranslationY(0);
            miButtonBack.setTranslationX(0);
        } else if (realPosition < adapter.getCount() - 1) {
            //Scroll away skip button
            if (buttonBackFunction == BUTTON_BACK_FUNCTION_SKIP) {
                boolean rtl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getResources().getConfiguration().getLayoutDirection() ==
                        View.LAYOUT_DIRECTION_RTL;
                miButtonBack.setTranslationX(positionOffset * (rtl ? 1 : -1) * miPager.getWidth());
            } else {
                miButtonBack.setTranslationX(0);
            }
        } else {
            //Keep skip button scrolled away, hide next button
            if (buttonBackFunction == BUTTON_BACK_FUNCTION_SKIP) {
                boolean rtl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && getResources().getConfiguration().getLayoutDirection() ==
                        View.LAYOUT_DIRECTION_RTL;
                miButtonBack.setTranslationX((rtl ? 1 : -1) * miPager.getWidth());
            } else {
                miButtonBack.setTranslationY(positionOffset * yOffset);
            }
        }
    }

    private void updateButtonNextPosition() {
        float realPosition = position + positionOffset;
        float yOffset = getResources().getDimensionPixelSize(R.dimen.mi_y_offset);

        if (realPosition < adapter.getCount() - 2) {
            //Reset
            miButtonNext.setTranslationY(0);
        } else if (realPosition < adapter.getCount() - 1) {
            //Reset finish button, hide next icon
            if (buttonNextFunction == BUTTON_NEXT_FUNCTION_NEXT_FINISH) {
                miButtonNext.setTranslationY(0);
            } else {
                miButtonNext.setTranslationY(positionOffset * yOffset);
            }
        } else if (realPosition >= adapter.getCount() - 1) {
            //Hide finish icon, keep next icon hidden
            if (buttonNextFunction == BUTTON_NEXT_FUNCTION_NEXT_FINISH) {
                miButtonNext.setTranslationY(positionOffset * yOffset);
            } else {
                miButtonNext.setTranslationY(-yOffset);
            }
        }
    }

    private void updatePagerIndicatorPosition() {
        float realPosition = position + positionOffset;
        float yOffset = getResources().getDimensionPixelSize(R.dimen.mi_y_offset);

        if (realPosition < adapter.getCount() - 1) {
            //Reset
            miPagerIndicator.setTranslationY(0);
        } else {
            //Hide CTA button
            miPagerIndicator.setTranslationY(positionOffset * yOffset);
        }
    }

    private void updateParallax() {
        if (position == getCount())
            return;

        Fragment fragment = getSlide(position).getFragment();
        Fragment fragmentNext = position < getCount() - 1 ?
                getSlide(position + 1).getFragment() : null;
        if (fragment instanceof Parallaxable) {
            ((Parallaxable) fragment).setOffset(positionOffset);
        }
        if (fragmentNext instanceof Parallaxable) {
            ((Parallaxable) fragmentNext).setOffset(-1 + positionOffset);
        }
    }

    private void updateFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (adapter != null && position + positionOffset > adapter.getCount() - 1) {
                setFullscreenFlags(false);
            } else {
                setFullscreenFlags(fullscreen);
            }
        }
    }

    private void updateBackgroundFade() {
        float realPosition = position + positionOffset;

        if (realPosition < adapter.getCount() - 1) {
            //Reset
            miFrame.setAlpha(1);
        } else {
            //Fade background
            miFrame.setAlpha(1 - (positionOffset * 0.5f));
        }
    }

    private void updateScrollPositions() {
        updateBackground();
        updateButtonCta();
        updateButtonBackPosition();
        updateButtonNextPosition();
        updatePagerIndicatorPosition();
        updateParallax();
        updateFullscreen();
        updateBackgroundFade();
    }

    private void updateButtonNextDrawable() {
        float realPosition = position + positionOffset;
        float offset = 0;

        if (buttonNextFunction == BUTTON_NEXT_FUNCTION_NEXT_FINISH) {
            if (realPosition >= adapter.getCount() - 1) {
                offset = 1;
            } else if (realPosition >= adapter.getCount() - 2) {
                offset = positionOffset;
            }
        }

        if (offset <= 0) {
            miButtonNext.setImageResource(R.drawable.mi_ic_next);
            miButtonNext.getDrawable().setAlpha(0xFF);
        } else {
            miButtonNext.setImageResource(R.drawable.mi_ic_next_finish);
            if (miButtonNext.getDrawable() != null && miButtonNext.getDrawable() instanceof LayerDrawable) {
                LayerDrawable drawable = (LayerDrawable) miButtonNext.getDrawable();
                drawable.getDrawable(0).setAlpha((int) (0xFF * (1 - offset)));
                drawable.getDrawable(1).setAlpha((int) (0xFF * offset));
            } else {
                miButtonNext.setImageResource(offset > 0 ? R.drawable.mi_ic_finish : R.drawable.mi_ic_next);
            }
        }
    }

    private void updateButtonBackDrawable() {
        if (buttonBackFunction == BUTTON_BACK_FUNCTION_SKIP) {
            miButtonBack.setImageResource(R.drawable.mi_ic_skip);
        } else {
            miButtonBack.setImageResource(R.drawable.mi_ic_previous);
        }
    }

    @SuppressWarnings("unused")
    public void autoplay(@IntRange(from = 1) long delay, @IntRange(from = -1) int repeatCount) {
        autoplayCounter = repeatCount;
        autoplayDelay = delay;
        autoplayCallback = new Runnable() {
            @Override
            public void run() {
                if (autoplayCounter == 0) {
                    cancelAutoplay();
                    return;
                }
                int distance = nextSlideAuto();
                if (distance != 0)
                    autoplayHandler.postDelayed(autoplayCallback, autoplayDelay + calculateScrollDuration(distance));
            }
        };
        autoplayHandler.postDelayed(autoplayCallback, autoplayDelay);
    }

    @SuppressWarnings("unused")
    public void autoplay(@IntRange(from = 1) long delay) {
        autoplay(delay, DEFAULT_AUTOPLAY_REPEAT_COUNT);
    }

    @SuppressWarnings("unused")
    public void autoplay(@IntRange(from = -1) int repeatCount) {
        autoplay(DEFAULT_AUTOPLAY_DELAY, repeatCount);
    }

    @SuppressWarnings("unused")
    public void autoplay() {
        autoplay(DEFAULT_AUTOPLAY_DELAY, DEFAULT_AUTOPLAY_REPEAT_COUNT);
    }

    @SuppressWarnings("unused")
    public void cancelAutoplay() {
        autoplayHandler.removeCallbacks(autoplayCallback);
        autoplayCallback = null;
        autoplayCounter = 0;
        autoplayDelay = 0;
    }

    @SuppressWarnings("unused")
    public boolean isAutoplaying() {
        return autoplayCallback != null;
    }

    @SuppressWarnings("unused")
    public long getPageScrollDuration() {
        return pageScrollDuration;
    }

    @SuppressWarnings("unused")
    public void setPageScrollDuration(@IntRange(from = 1) long pageScrollDuration) {
        this.pageScrollDuration = pageScrollDuration;
    }

    @SuppressWarnings("unused")
    public Interpolator getPageScrollInterpolator() {
        return pageScrollInterpolator;
    }

    @SuppressWarnings("unused")
    public void setPageScrollInterpolator(Interpolator pageScrollInterpolator) {
        this.pageScrollInterpolator = pageScrollInterpolator;
    }

    @SuppressWarnings("unused")
    public void setPageScrollInterpolator(@InterpolatorRes int interpolatorRes) {
        this.pageScrollInterpolator = AnimationUtils.loadInterpolator(this, interpolatorRes);
    }

    @SuppressWarnings("unused")
    public boolean isFullscreen() {
        return fullscreen;
    }

    @SuppressWarnings("unused")
    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    @SuppressWarnings("unused")
    public boolean isButtonCtaVisible() {
        return buttonCtaVisible;
    }

    @SuppressWarnings("unused")
    public void setButtonCtaVisible(boolean buttonCtaVisible) {
        this.buttonCtaVisible = buttonCtaVisible;
        updateButtonCta();
    }

    @ButtonCtaTintMode
    @SuppressWarnings("unused")
    public int getButtonCtaTintMode() {
        return buttonCtaTintMode;
    }

    @SuppressWarnings("unused")
    public void setButtonCtaTintMode(@ButtonCtaTintMode int buttonCtaTintMode) {
        this.buttonCtaTintMode = buttonCtaTintMode;
    }

    @ButtonBackFunction
    @SuppressWarnings("unused")
    public int getButtonBackFunction() {
        return buttonBackFunction;
    }

    @SuppressWarnings("unused")
    public void setButtonBackFunction(@ButtonBackFunction int buttonBackFunction) {
        this.buttonBackFunction = buttonBackFunction;
        switch (buttonBackFunction) {
            case BUTTON_BACK_FUNCTION_BACK:
                CheatSheet.setup(miButtonBack, R.string.mi_content_description_back);
                break;
            case BUTTON_BACK_FUNCTION_SKIP:
                CheatSheet.setup(miButtonBack, R.string.mi_content_description_skip);
                break;
        }
        updateButtonBackDrawable();
        updateButtonBackPosition();
    }

    @Deprecated
    @SuppressWarnings("unused")
    public boolean isSkipEnabled() {
        return buttonBackFunction == BUTTON_BACK_FUNCTION_SKIP;
    }

    @Deprecated
    @SuppressWarnings("unused")
    public void setSkipEnabled(boolean skipEnabled) {
        setButtonBackFunction(skipEnabled ? BUTTON_BACK_FUNCTION_SKIP : BUTTON_BACK_FUNCTION_BACK);
    }

    @ButtonNextFunction
    @SuppressWarnings("unused")
    public int getButtonNextFunction() {
        return buttonNextFunction;
    }

    public void setButtonNextFunction(@ButtonNextFunction int buttonNextFunction) {
        this.buttonNextFunction = buttonNextFunction;
        switch (buttonNextFunction) {
            case BUTTON_NEXT_FUNCTION_NEXT_FINISH:
                CheatSheet.setup(miButtonNext, R.string.mi_content_description_next_finish);
                break;
            case BUTTON_NEXT_FUNCTION_NEXT:
                CheatSheet.setup(miButtonNext, R.string.mi_content_description_next);
                break;
        }
        updateButtonNextDrawable();
        updateButtonNextPosition();
    }

    public View getContentView() {
        return findViewById(android.R.id.content);
    }

    @Deprecated
    @SuppressWarnings("unused")
    public boolean isFinishEnabled() {
        return buttonNextFunction == BUTTON_NEXT_FUNCTION_NEXT_FINISH;
    }

    @Deprecated
    @SuppressWarnings("unused")
    public void setFinishEnabled(boolean finishEnabled) {
        setButtonNextFunction(finishEnabled ? BUTTON_NEXT_FUNCTION_NEXT_FINISH : BUTTON_NEXT_FUNCTION_NEXT);
    }

    @SuppressWarnings("unused")
    public boolean isButtonBackVisible() {
        return miButtonBack.getVisibility() == View.VISIBLE;
    }

    @SuppressWarnings("unused")
    public void setButtonBackVisible(boolean visible) {
        miButtonBack.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @SuppressWarnings("unused")
    public boolean isButtonNextVisible() {
        return miButtonNext.getVisibility() == View.VISIBLE;
    }

    @SuppressWarnings("unused")
    public void setButtonNextVisible(boolean visible) {
        miButtonNext.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    @SuppressWarnings("unused")
    public boolean isPagerIndicatorVisible() {
        return miPagerIndicator.getVisibility() == View.VISIBLE;
    }

    @SuppressWarnings("unused")
    public void setPagerIndicatorVisible(boolean visible) {
        miPagerIndicator.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Deprecated
    @SuppressWarnings("deprecation,unused")
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        miPager.setOnPageChangeListener(listener);
        miPager.addOnPageChangeListener(this.listener);
    }

    @SuppressWarnings("unused")
    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        miPager.addOnPageChangeListener(listener);
    }

    @SuppressWarnings("unused")
    public void removeOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        if (listener != this.listener)
            miPager.removeOnPageChangeListener(listener);
    }

    @SuppressWarnings("unused")
    public View.OnClickListener getButtonCtaClickListener() {
        return buttonCtaClickListener;
    }

    @SuppressWarnings("unused")
    public void setButtonCtaClickListener(View.OnClickListener buttonCtaClickListener) {
        this.buttonCtaClickListener = buttonCtaClickListener;
        updateButtonCta();
    }

    @SuppressWarnings("unused")
    public CharSequence getButtonCtaLabel() {
        if (buttonCtaLabel != null)
            return buttonCtaLabel;
        return getString(buttonCtaLabelRes);
    }

    @SuppressWarnings("unused")
    public void setButtonCtaLabel(@StringRes int buttonCtaLabelRes) {
        this.buttonCtaLabelRes = buttonCtaLabelRes;
        this.buttonCtaLabel = null;
        updateButtonCta();
    }

    @SuppressWarnings("unused")
    public void setButtonCtaLabel(CharSequence buttonCtaLabel) {
        this.buttonCtaLabel = buttonCtaLabel;
        this.buttonCtaLabelRes = 0;
        updateButtonCta();
    }

    @SuppressWarnings("unused")
    public void setNavigationPolicy(NavigationPolicy navigationPolicy) {
        this.navigationPolicy = navigationPolicy;
    }

    @SuppressWarnings("unused")
    public void addOnNavigationBlockedListener(OnNavigationBlockedListener listener) {
        navigationBlockedListeners.add(listener);
    }

    @SuppressWarnings("unused")
    public void removeOnNavigationBlockedListener(OnNavigationBlockedListener listener) {
        navigationBlockedListeners.remove(listener);
    }

    @SuppressWarnings("unused")
    public void clearOnNavigationBlockedListeners() {
        navigationBlockedListeners.clear();
    }

    @SuppressWarnings("unused")
    public void lockSwipeIfNeeded() {
        if (position < getCount()) {
            miPager.setSwipeLeftEnabled(canGoForward(position, false));
            miPager.setSwipeRightEnabled(canGoBackward(position, false));
        }
    }

    @SuppressWarnings("unused")
    public void addSlide(int location, Slide object) {
        adapter.addSlide(location, object);
        notifyDataSetChanged();
    }

    @SuppressWarnings("unused")
    public boolean addSlide(Slide object) {
        boolean modified = adapter.addSlide(object);
        if (modified) {
            notifyDataSetChanged();
        }
        return modified;
    }

    @SuppressWarnings("unused")
    public boolean addSlides(int location, @NonNull Collection<? extends Slide> collection) {
        boolean modified = adapter.addSlides(location, collection);
        if (modified) {
            notifyDataSetChanged();
        }
        return modified;
    }

    @SuppressWarnings("unused")
    public boolean addSlides(@NonNull Collection<? extends Slide> collection) {
        boolean modified = adapter.addSlides(collection);
        if (modified) {
            notifyDataSetChanged();
        }
        return modified;
    }

    @SuppressWarnings("unused")
    public boolean clearSlides() {
        if (adapter.clearSlides()) {
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    public boolean containsSlide(Object object) {
        return adapter.containsSlide(object);
    }

    @SuppressWarnings("unused")
    public boolean containsSlides(@NonNull Collection<?> collection) {
        return adapter.containsSlides(collection);
    }

    @SuppressWarnings("unused")
    public Slide getSlide(int location) {
        return adapter.getSlide(location);
    }

    @SuppressWarnings("unused")
    public int getSlidePosition(Slide slide) {
        return adapter.getItemPosition(slide);
    }

    @SuppressWarnings("unused")
    public int getCurrentSlidePosition() {
        return miPager.getCurrentItem();
    }

    @SuppressWarnings("unused")
    public Fragment getItem(int position) {
        return adapter.getItem(position);
    }

    @ColorRes
    @SuppressWarnings("unused")
    public int getBackground(int position) {
        return adapter.getBackground(position);
    }

    @ColorRes
    @SuppressWarnings("unused")
    public int getBackgroundDark(int position) {
        return adapter.getBackgroundDark(position);
    }

    @SuppressWarnings("unused")
    public List<Slide> getSlides() {
        return adapter.getSlides();
    }

    @SuppressWarnings("unused")
    public int indexOfSlide(Object object) {
        return adapter.indexOfSlide(object);
    }

    @SuppressWarnings("unused")
    public boolean isEmpty() {
        return adapter.isEmpty();
    }

    @SuppressWarnings("unused")
    public int getCount() {
        return adapter == null ? 0 : adapter.getCount();
    }

    @SuppressWarnings("unused")
    public int lastIndexOfSlide(Object object) {
        return adapter.lastIndexOfSlide(object);
    }

    @SuppressWarnings("unused")
    public Slide removeSlide(int location) {
        Slide object = adapter.removeSlide(location);
        notifyDataSetChanged();
        return object;
    }

    @SuppressWarnings("unused")
    public boolean removeSlide(Object object) {
        boolean modified = adapter.removeSlide(object);
        if (modified) {
            notifyDataSetChanged();
        }
        return modified;
    }

    @SuppressWarnings("unused")
    public boolean removeSlides(@NonNull Collection<?> collection) {
        boolean modified = adapter.removeSlides(collection);
        if (modified) {
            notifyDataSetChanged();
        }
        return modified;
    }

    @SuppressWarnings("unused")
    public boolean retainSlides(@NonNull Collection<?> collection) {
        boolean modified = adapter.retainSlides(collection);
        if (modified) {
            notifyDataSetChanged();
        }
        return modified;
    }

    @SuppressWarnings("unused")
    public Slide setSlide(int location, Slide object) {
        Slide oldObject = adapter.setSlide(location, object);
        notifyDataSetChanged();
        return oldObject;
    }

    @SuppressWarnings("unused")
    public List<Slide> setSlides(List<? extends Slide> list) {
        List<Slide> oldList = adapter.setSlides(list);
        notifyDataSetChanged();
        return oldList;
    }

    public void setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        miPager.setPageTransformer(reverseDrawingOrder, transformer);
    }

    public void notifyDataSetChanged() {
        if (!activityCreated) {
            // Don't notify any listener until the activity is created
            return;
        }

        int position = this.position;
        miPager.setAdapter(adapter);
        miPager.setCurrentItem(position);

        if (finishIfNeeded()) {
            return;
        }

        updateTaskDescription();
        updateButtonBackDrawable();
        updateButtonNextDrawable();
        updateScrollPositions();
        lockSwipeIfNeeded();
    }

    private class IntroPageChangeListener extends FadeableViewPager.SimpleOnOverscrollPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            IntroActivity.this.position = (int) Math.floor(position + positionOffset);
            IntroActivity.this.positionOffset = (((position + positionOffset) % 1) + 1) % 1;

            if (finishIfNeeded()) {
                return;
            }

            //Lock while scrolling a slide near its edges to lock (uncommon) multiple page swipes
            if (Math.abs(positionOffset) < 0.1f) {
                lockSwipeIfNeeded();
            }

            updateButtonNextDrawable();
            updateScrollPositions();
        }

        @Override
        public void onPageSelected(int position) {
            IntroActivity.this.position = position;
            updateTaskDescription();
            lockSwipeIfNeeded();
        }
    }

    private class ButtonCtaClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            goToSlide(getCount());
        }
    }
}
