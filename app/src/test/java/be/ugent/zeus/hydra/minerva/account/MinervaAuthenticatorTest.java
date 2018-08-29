package be.ugent.zeus.hydra.minerva.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Intent;
import android.os.Bundle;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.minerva.auth.AuthActivity;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAccountManager;
import org.threeten.bp.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.robolectric.Shadows.shadowOf;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class MinervaAuthenticatorTest {

    private MinervaAuthenticator authenticator;
    private AccountAuthenticatorResponse response;
    private Account testAccount = new Account("TEST", MinervaConfig.ACCOUNT_TYPE);

    @Before
    public void setUp() {
        this.authenticator = new MinervaAuthenticator(RuntimeEnvironment.application);
        this.response = mock(AccountAuthenticatorResponse.class);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void editProperties() {
        authenticator.editProperties(response, "");
    }

    @Test
    public void addAccount() {
        Bundle bundle = authenticator.addAccount(response, MinervaConfig.ACCOUNT_TYPE, MinervaConfig.DEFAULT_SCOPE, null, Bundle.EMPTY);
        Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
        assertNotNull(intent);

        assertNotNull(intent.getComponent());
        assertEquals(AuthActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(MinervaConfig.ACCOUNT_TYPE, intent.getStringExtra(AuthActivity.ARG_ACCOUNT_TYPE));
        assertTrue(intent.getBooleanExtra(AuthActivity.ARG_ADDING_NEW_ACCOUNT, false));
        assertEquals(MinervaConfig.DEFAULT_SCOPE, intent.getStringExtra(AuthActivity.ARG_AUTH_TYPE));
        assertEquals(response, intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));
        assertEquals(Bundle.EMPTY, intent.getBundleExtra(AuthActivity.ARG_EXTRA_BUNDLE));
    }

    @Test
    public void confirmCredentials() {
        Bundle bundle = authenticator.confirmCredentials(response, testAccount, new Bundle());
        Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
        assertNotNull(intent);

        assertNotNull(intent.getComponent());
        assertEquals(AuthActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(MinervaConfig.ACCOUNT_TYPE, intent.getStringExtra(AuthActivity.ARG_ACCOUNT_TYPE));
        assertFalse(intent.getBooleanExtra(AuthActivity.ARG_ADDING_NEW_ACCOUNT, false));
        assertEquals(response, intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));
    }

    /**
     * Test if the existing code is present and not expired.
     */
    @Test
    public void getAuthTokenExiting() throws NetworkErrorException {

        final String testToken = "TEST_TOKEN_AUTH2";

        AccountManager manager = AccountManager.get(RuntimeEnvironment.application);
        ShadowAccountManager shadowManager = shadowOf(manager);
        shadowManager.addAccount(testAccount);
        manager.setAuthToken(testAccount, MinervaConfig.DEFAULT_SCOPE, testToken);
        // The token expires in the future.
        manager.setUserData(testAccount, MinervaAuthenticator.EXP_DATE,
                LocalDateTime.now().plusMonths(1).format(AccountUtils.FORMATTER));

        Bundle result = authenticator.getAuthToken(response, testAccount, MinervaConfig.DEFAULT_SCOPE, null);

        assertEquals(testAccount.name, result.getString(AccountManager.KEY_ACCOUNT_NAME));
        assertEquals(testAccount.type, result.getString(AccountManager.KEY_ACCOUNT_TYPE));
        assertEquals(testToken, result.getString(AccountManager.KEY_AUTHTOKEN));
    }

    @Test
    public void getAuthTokenExpiredOK() throws NetworkErrorException {

        final String expiredToken = "TEST_TOKEN_AUTH2_EXPIRED";
        final String goodToken = "TEST_TOKEN_AUTH2_FRESH";
        final String password = "TEST_PASSWORD";

        final MutableObject<String> mutableObject = new MutableObject<>();

        // We subclass this to stub out network-related and difficult methods.
        // The AccountManager will still use the normal authenticator; this is not a problem.
        MinervaAuthenticator authenticator = new MinervaAuthenticator(RuntimeEnvironment.application) {
            @Override
            String getRefreshAccessToken(Account account, String refreshToken) {
                mutableObject.setValue(refreshToken);
                return goodToken;
            }
        };

        AccountManager manager = AccountManager.get(RuntimeEnvironment.application);
        ShadowAccountManager shadowManager = shadowOf(manager);
        shadowManager.addAccount(testAccount);
        manager.setAuthToken(testAccount, MinervaConfig.DEFAULT_SCOPE, expiredToken);
        // The token expires in the future.
        manager.setUserData(testAccount, MinervaAuthenticator.EXP_DATE,
                LocalDateTime.now().minusMonths(1).format(AccountUtils.FORMATTER));
        manager.setPassword(testAccount, password);

        Bundle result = authenticator.getAuthToken(response, testAccount, MinervaConfig.DEFAULT_SCOPE, null);

        assertEquals(testAccount.name, result.getString(AccountManager.KEY_ACCOUNT_NAME));
        assertEquals(testAccount.type, result.getString(AccountManager.KEY_ACCOUNT_TYPE));
        assertEquals(goodToken, result.getString(AccountManager.KEY_AUTHTOKEN));
        assertEquals(password, mutableObject.getValue());
    }

    @Test
    public void getAuthTokenExpiredNoPassword() throws NetworkErrorException {

        final String expiredToken = "TEST_TOKEN_AUTH2_EXPIRED";

        AccountManager manager = AccountManager.get(RuntimeEnvironment.application);
        ShadowAccountManager shadowManager = shadowOf(manager);
        shadowManager.addAccount(testAccount);
        manager.setAuthToken(testAccount, MinervaConfig.DEFAULT_SCOPE, expiredToken);
        // The token expires in the future.
        manager.setUserData(testAccount, MinervaAuthenticator.EXP_DATE,
                LocalDateTime.now().minusMonths(1).format(AccountUtils.FORMATTER));

        Bundle result = authenticator.getAuthToken(response, testAccount, MinervaConfig.DEFAULT_SCOPE, null);

        Intent intent = result.getParcelable(AccountManager.KEY_INTENT);
        assertNotNull(intent);

        assertNotNull(intent.getComponent());
        assertEquals(AuthActivity.class.getName(), intent.getComponent().getClassName());
        assertEquals(response, intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE));
        assertEquals(testAccount.type, intent.getStringExtra(AuthActivity.ARG_ACCOUNT_TYPE));
        assertEquals(MinervaConfig.DEFAULT_SCOPE, intent.getStringExtra(AuthActivity.ARG_AUTH_TYPE));
    }

    @Test(expected = NetworkErrorException.class)
    public void getAuthTokenExpiredNoNetwork() throws NetworkErrorException {

        // We subclass this to stub out network-related and difficult methods.
        // The AccountManager will still use the normal authenticator; this is not a problem.
        MinervaAuthenticator authenticator = new MinervaAuthenticator(RuntimeEnvironment.application) {
            @Override
            String getRefreshAccessToken(Account account, String refreshToken) throws NetworkErrorException {
                throw new NetworkErrorException();
            }
        };

        AccountManager manager = AccountManager.get(RuntimeEnvironment.application);
        ShadowAccountManager shadowManager = shadowOf(manager);
        shadowManager.addAccount(testAccount);
        manager.setAuthToken(testAccount, MinervaConfig.DEFAULT_SCOPE, "test");
        // The token expires in the future.
        manager.setUserData(testAccount, MinervaAuthenticator.EXP_DATE,
                LocalDateTime.now().minusMonths(1).format(AccountUtils.FORMATTER));
        manager.setPassword(testAccount, "test");

        authenticator.getAuthToken(response, testAccount, MinervaConfig.DEFAULT_SCOPE, null);
    }

    @Test
    public void getAuthTokenLabel() {
        assertNull(authenticator.getAuthTokenLabel("Test_Type"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void updateCredentials() {
        authenticator.updateCredentials(response, testAccount, "", new Bundle());
    }

    @Test
    public void hasFeatures() {
        Bundle result = authenticator.hasFeatures(response, testAccount, new String[]{});
        assertFalse(result.getBoolean(AccountManager.KEY_BOOLEAN_RESULT));
    }
}