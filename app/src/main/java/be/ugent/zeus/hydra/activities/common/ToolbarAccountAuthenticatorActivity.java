package be.ugent.zeus.hydra.activities.common;

import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;

/**
 * This activity is the same as the framework's {@link android.accounts.AccountAuthenticatorActivity}, but for use with
 * {@link android.support.v7.app.AppCompatActivity}.
 * <p>
 * Base class for implementing an Activity that is used to help implement an
 * AbstractAccountAuthenticator. If the AbstractAccountAuthenticator needs to use an activity
 * to handle the request then it can have the activity extend AccountAuthenticatorActivity.
 * The AbstractAccountAuthenticator passes in the response to the intent using the following:
 * <pre>
 *      intent.putExtra({@link AccountManager#KEY_ACCOUNT_AUTHENTICATOR_RESPONSE}, response);
 * </pre>
 * The activity then sets the result that is to be handed to the response via
 * {@link #setAccountAuthenticatorResult(android.os.Bundle)}.
 * This result will be sent as the result of the request when the activity finishes. If this
 * is never set or if it is set to null then error {@link AccountManager#ERROR_CODE_CANCELED}
 * will be called on the response.
 * <p>
 * TODO: merge with our AuthActivity
 *
 * @author Niko Strijbol
 * @see <a href="https://github.com/android/platform_frameworks_base/blob/master/core/java/android/accounts/AccountAuthenticatorActivity.java">AOSP</a>
 */
public class ToolbarAccountAuthenticatorActivity extends HydraActivity {

    //Extra bundle
    public static final String ARG_EXTRA_BUNDLE = "extraBundle";
    //Bundle key to provide up navigation or just finish the activity?
    public static final String KEY_PROVIDE_UP_NAVIGATION = "provideUpNavigation";
    private static final String TAG = "ToolbarAcAuthActivity";
    private AccountAuthenticatorResponse response = null;
    private Bundle resultBundle = null;
    private Bundle extra = null;

    /**
     * Set the result that is to be sent as the result of the request that caused this Activity to be launched. If
     * result is null or this method is never called then the request will be canceled.
     *
     * @param result this is returned as the result of the AbstractAccountAuthenticator request
     */
    public final void setAccountAuthenticatorResult(Bundle result) {
        resultBundle = result;
    }

    /**
     * Retrieves the AccountAuthenticatorResponse from either the intent of the icicle, if the icicle is non-zero.
     *
     * @param icicle the save instance data of this Activity, may be null
     */
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        response = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);

        if (response != null) {
            response.onRequestContinued();
        }

        if (getIntent().hasExtra(ARG_EXTRA_BUNDLE)) {
            extra = getIntent().getBundleExtra(ARG_EXTRA_BUNDLE);
        }
    }

    /**
     * Sends the result or a Constants.ERROR_CODE_CANCELED error if a result isn't present.
     */
    public void finishUp() {
        if (response != null) {
            // send the result bundle back if set, otherwise send an error.
            if (resultBundle != null) {
                response.onResult(resultBundle);
            } else {
                response.onError(AccountManager.ERROR_CODE_CANCELED, "canceled");
            }
            response = null;
        }

        if (extra != null) {
            Log.d(TAG, "Extra's were found.");
            if (extra.getBoolean(KEY_PROVIDE_UP_NAVIGATION, false)) {
                NavUtils.navigateUpFromSameTask(this);
            } else {
                finish();
            }
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }
}