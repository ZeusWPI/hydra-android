package be.ugent.zeus.hydra.ui.onboarding;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.auth.AccountUtils;
import be.ugent.zeus.hydra.data.auth.MinervaConfig;
import be.ugent.zeus.hydra.data.sync.SyncUtils;
import be.ugent.zeus.hydra.data.sync.minerva.MinervaAdapter;
import be.ugent.zeus.hydra.ui.minerva.AuthActivity;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import java.io.IOException;

/**
 * Show onboarding for new users.
 *
 * @author Niko Strijbol
 */
public class OnboardingActivity extends IntroActivity implements View.OnClickListener {

    private static final String TAG = "OnboardingActivity";

    private FirebaseAnalytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        analytics = FirebaseAnalytics.getInstance(this);
        // Log start of onboarding
        analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_BEGIN, null);

        //First tab
        addSlide(new SimpleSlide.Builder()
                .title(R.string.onboarding_welcome)
                .description(R.string.onboarding_welcome_description)
                .image(R.drawable.logo_onboarding_ugent)
                .background(R.color.ugent_blue_medium)
                .backgroundDark(R.color.ugent_blue_dark)
                .build());

        //Home feed selector
        addSlide(new FragmentSlide.Builder()
                .background(R.color.ugent_blue_medium)
                .backgroundDark(R.color.ugent_blue_dark)
                .fragment(new HomeFeedFragment())
                .build());

        //Check if the user already has an account. This is possible, because clearing the application data does not
        //remove the account.
        if (!AccountUtils.hasAccount(this)) {
            addSlide(new SimpleSlide.Builder()
                    .title(R.string.onboarding_minerva_title)
                    .description(R.string.onboarding_minerva_description)
                    .image(R.drawable.logo_onboarding_minerva)
                    .background(R.color.ugent_blue_medium)
                    .backgroundDark(R.color.ugent_blue_dark)
                    .buttonCtaLabel(R.string.onboarding_minerva_button)
                    .buttonCtaClickListener(this)
                    .build());
        }
    }

    @Override
    public void onClick(final View v) {
        AccountManager manager = AccountManager.get(this);

        Bundle options = new Bundle();
        options.putBoolean(AuthActivity.KEY_PROVIDE_UP_NAVIGATION, false);

        manager.addAccount(MinervaConfig.ACCOUNT_TYPE, MinervaConfig.DEFAULT_SCOPE, null, options, this, accountManagerFuture -> {
            try {
                v.setVisibility(View.GONE);
                Bundle result = accountManagerFuture.getResult();
                Log.d(TAG, "Account " + result.getString(AccountManager.KEY_ACCOUNT_NAME) + " was created.");
                onAccountCreated();
            } catch (OperationCanceledException e) {
                Toast.makeText(getApplicationContext(), R.string.minerva_no_permission, Toast.LENGTH_LONG).show();
            } catch (IOException | AuthenticatorException e) {
                Log.w(TAG, "Account not added.", e);
            }
        }, null);
    }

    private void onAccountCreated() {
        //Get an account
        Account account = AccountUtils.getAccount(this);
        Bundle bundle = new Bundle();
        bundle.putBoolean(MinervaAdapter.EXTRA_FIRST_SYNC, true);
        SyncUtils.requestSync(account, MinervaConfig.SYNC_AUTHORITY, bundle);

        // Log sign in
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, null);
    }
}