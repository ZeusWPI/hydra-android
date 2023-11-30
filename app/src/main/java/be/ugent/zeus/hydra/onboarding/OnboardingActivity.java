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

package be.ugent.zeus.hydra.onboarding;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.WindowCompat;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.reporting.Event;
import be.ugent.zeus.hydra.common.reporting.Reporting;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Show onboarding for new users.
 *
 * @author Niko Strijbol
 */
public class OnboardingActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        Reporting.getTracker(this)
                .log(new TutorialBeginEvent());

        //First tab
        addSlide(new SimpleSlide.Builder()
                .title(R.string.onboarding_welcome)
                .description(R.string.onboarding_welcome_description)
                .image(R.drawable.logo_onboarding_ugent)
                .build());

        if (Reporting.hasReportingOptions()) {
            // Check for permission for data collection
            addSlide(new FragmentSlide.Builder()
                    .fragment(new ReportingFragment())
                    .build()
            );
        }

        // Home feed selector
        addSlide(new FragmentSlide.Builder()
                .fragment(new HomeFeedFragment())
                .build());
    }

    private static final class TutorialBeginEvent implements Event {
        @Nullable
        @Override
        public String eventName() {
            return Reporting.getEvents().tutorialBegin();
        }
    }
}
