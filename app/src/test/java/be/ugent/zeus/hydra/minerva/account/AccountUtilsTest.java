package be.ugent.zeus.hydra.minerva.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import okhttp3.HttpUrl;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAccountManager;
import org.threeten.bp.LocalDateTime;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

/**
 * @author Niko Strijbol
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class AccountUtilsTest {

    @Test
    public void hasAccount() {

        // Test that there are no accounts
        assertFalse(AccountUtils.hasAccount(RuntimeEnvironment.application));

        // Add a test account
        ShadowAccountManager manager = shadowOf(AccountManager.get(RuntimeEnvironment.application));
        Account testAccount = new Account("TEST", MinervaConfig.ACCOUNT_TYPE);
        manager.addAccount(testAccount);

        // Test the actual method
        assertTrue(AccountUtils.hasAccount(RuntimeEnvironment.application));
    }

    @Test
    public void getAccount() {
        ShadowAccountManager manager = shadowOf(AccountManager.get(RuntimeEnvironment.application));
        Account testAccount = new Account("TEST", MinervaConfig.ACCOUNT_TYPE);
        manager.addAccount(testAccount);

        assertEquals(testAccount, AccountUtils.getAccount(RuntimeEnvironment.application));
    }

    @Test
    public void getExpirationDate() {
        AccountManager accountManager = AccountManager.get(RuntimeEnvironment.application);
        ShadowAccountManager manager = shadowOf(accountManager);
        Account testAccount = new Account("TEST", MinervaConfig.ACCOUNT_TYPE);
        manager.addAccount(testAccount);

        LocalDateTime testValue = LocalDateTime.parse(
                LocalDateTime.now().format(AccountUtils.FORMATTER), AccountUtils.FORMATTER);
        accountManager
                .setUserData(testAccount, MinervaAuthenticator.EXP_DATE, testValue.format(AccountUtils.FORMATTER));

        assertEquals(testValue, AccountUtils.getExpirationDate(accountManager, testAccount));
    }

    @Test
    public void getRequestUri() throws Exception {

        String url = AccountUtils.getRequestUri();

        // Do some testing, we don't test if the URL is exactly right, as we can't do that.
        assertThat(url, startsWith(MinervaConfig.AUTHORIZATION_ENDPOINT));

        HttpUrl httpUrl = HttpUrl.parse(url);
        assert httpUrl != null;

        assertEquals(BuildConfig.OAUTH_ID, URLDecoder.decode(httpUrl.queryParameter(OAuth.OAUTH_CLIENT_ID), StandardCharsets.UTF_8.name()));
        assertEquals(MinervaConfig.CALLBACK_URI, URLDecoder.decode(httpUrl.queryParameter(OAuth.OAUTH_REDIRECT_URI), "UTF-8"));
        assertEquals(ResponseType.CODE.toString(), httpUrl.queryParameter(OAuth.OAUTH_RESPONSE_TYPE));
    }
}