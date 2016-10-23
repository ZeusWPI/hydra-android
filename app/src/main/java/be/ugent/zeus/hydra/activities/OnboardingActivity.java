package be.ugent.zeus.hydra.activities;

import android.accounts.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.common.ToolbarAccountAuthenticatorActivity;
import be.ugent.zeus.hydra.fragments.onboarding.HomeFeedFragment;
import be.ugent.zeus.hydra.minerva.auth.AccountUtils;
import be.ugent.zeus.hydra.minerva.auth.MinervaConfig;
import be.ugent.zeus.hydra.minerva.sync.SyncUtils;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import java.io.IOException;

/**
 * Show onboarding for new users.
 */
public class OnboardingActivity extends IntroActivity implements View.OnClickListener {

    private static final String TAG = "OnboardingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //First tab
        addSlide(new SimpleSlide.Builder()
                .title("Welkom bij Hydra")
                .description("Kies wat je wilt zien en doen in de app")
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
        if(!AccountUtils.hasAccount(this)) {
            addSlide(new SimpleSlide.Builder()
                    .title("Minerva")
                    .description("Meld je aan bij Minerva om je vakken te kunnen bekijken")
                    .image(R.drawable.logo_onboarding_minerva)
                    .background(R.color.ugent_blue_medium)
                    .backgroundDark(R.color.ugent_blue_dark)
                    .buttonCtaLabel("Aanmelden")
                    .buttonCtaClickListener(this)
                    .build());
        }
    }

    @Override
    public void onClick(final View v) {
        AccountManager manager = AccountManager.get(this);

        Bundle options = new Bundle();
        options.putBoolean(ToolbarAccountAuthenticatorActivity.KEY_PROVIDE_UP_NAVIGATION, false);

        manager.addAccount(MinervaConfig.ACCOUNT_TYPE, MinervaConfig.DEFAULT_SCOPE, null, options, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                try {
                    v.setVisibility(View.GONE);
                    Bundle result = accountManagerFuture.getResult();
                    Log.d(TAG, "Account " + result.getString(AccountManager.KEY_ACCOUNT_NAME) + " was created.");
                    onAccountCreated();
                } catch (OperationCanceledException e) {
                    Toast.makeText(getApplicationContext(), "Je gaf geen toestemming om je account te gebruiken.", Toast.LENGTH_LONG).show();
                } catch (IOException | AuthenticatorException e) {
                    Log.w(TAG, "Account not added.", e);
                }
            }
        }, null);
    }

    private void onAccountCreated() {
        //Get an account
        Account account = AccountUtils.getAccount(this);
        SyncUtils.enableSync(this, account);
        //Enable broadcasts, because the user might go to the fragment.
        SyncUtils.requestFirstSync(account);
    }
}