package be.ugent.zeus.hydra.onboarding;

import android.os.Bundle;

import androidx.annotation.Nullable;

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

        Reporting.getTracker(this)
                .log(new TutorialBeginEvent());

        //First tab
        addSlide(new SimpleSlide.Builder()
                .title(R.string.onboarding_welcome)
                .description(R.string.onboarding_welcome_description)
                .image(R.drawable.logo_onboarding_ugent)
                .background(R.color.hydra_primary_colour)
                .backgroundDark(R.color.hydra_primary_dark_colour)
                .build());

        // Check for permission for data collection
        addSlide(new FragmentSlide.Builder()
                .background(R.color.hydra_primary_colour)
                .backgroundDark(R.color.hydra_primary_dark_colour)
                .fragment(new ReportingFragment())
                .build()
        );

        // Home feed selector
        addSlide(new FragmentSlide.Builder()
                .background(R.color.hydra_primary_colour)
                .backgroundDark(R.color.hydra_primary_dark_colour)
                .fragment(new HomeFeedFragment())
                .build());
    }

    private static final class TutorialBeginEvent implements Event {
        @Nullable
        @Override
        public String getEventName() {
            return Reporting.getEvents().tutorialBegin();
        }
    }
}