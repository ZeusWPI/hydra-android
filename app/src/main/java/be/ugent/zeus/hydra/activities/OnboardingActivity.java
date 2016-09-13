package be.ugent.zeus.hydra.activities;

import android.os.Bundle;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.fragments.onboarding.HomeFeedFragment;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Show onboarding for new users.
 */
public class OnboardingActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //First tab
        addSlide(new SimpleSlide.Builder()
                .title("Welkom bij Hydra")
                .description("Kies wat je wilt zien en doen in de app")
                .image(R.drawable.hydra_columns)
                .background(R.color.ugent_blue_medium)
                .backgroundDark(R.color.ugent_blue_dark)
                .build());


        addSlide(new FragmentSlide.Builder()
                .background(R.color.ugent_blue_medium)
                .backgroundDark(R.color.ugent_blue_dark)
                .fragment(new HomeFeedFragment())
                .build());
    }
}