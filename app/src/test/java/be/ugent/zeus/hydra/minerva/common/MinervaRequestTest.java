package be.ugent.zeus.hydra.minerva.common;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import be.ugent.zeus.hydra.common.network.IOFailureException;
import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import be.ugent.zeus.hydra.minerva.account.MinervaConfig;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * This test contains all non-cache related tests from {@link be.ugent.zeus.hydra.common.network.JsonOkHttpRequestTest}.
 *
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class MinervaRequestTest {

    private MockWebServer server;
    private Account account = new Account("TEST", MinervaConfig.ACCOUNT_TYPE);

    @Before
    public void setUp() {
        server = new MockWebServer();
    }

    @After
    public void breakDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void testFromNetworkFine() throws IOException {
        server.enqueue(integerJsonResponse());
        server.start();

        HttpUrl url = server.url("/fine.json");

        SimpleTestRequest request = new SimpleTestRequest(url, account, "test");
        Result<Integer> result = request.performRequest(null);

        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());
    }

    @Test(expected = RequestException.class)
    public void testWrongFormatEmpty() throws IOException, RequestException {
        server.enqueue(new MockResponse());
        server.start();
        HttpUrl url = server.url("/fine.json");

        SimpleTestRequest request = new SimpleTestRequest(url, account, "test");
        Result<Integer> result = request.performRequest(null);
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = RequestException.class)
    public void testWrongFormatString() throws IOException, RequestException {
        server.enqueue(new MockResponse().setBody("\"TEST\""));
        server.start();
        HttpUrl url = server.url("/fine.json");

        SimpleTestRequest request = new SimpleTestRequest(url, account, "test");
        Result<Integer> result = request.performRequest(null);
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = RequestException.class)
    public void testWrongFormatText() throws IOException, RequestException {
        server.enqueue(new MockResponse().setBody("TEST"));
        server.start();
        HttpUrl url = server.url("/fine.json");

        SimpleTestRequest request = new SimpleTestRequest(url, account, "test");
        Result<Integer> result = request.performRequest(null);
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = RequestException.class)
    public void testWrongFormatObject() throws IOException, RequestException {
        server.enqueue(new MockResponse().setBody("{\n" +
                "\t   \"rank\": \"4\",\n" +
                "\t   \"suit\": \"CLUBS\"\n" +
                "\t }"));
        server.start();
        HttpUrl url = server.url("/fine.json");

        SimpleTestRequest request = new SimpleTestRequest(url, account, "test");
        Result<Integer> result = request.performRequest(null);
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = RequestException.class)
    public void testOffline() throws IOException, RequestException {
        server.start();
        HttpUrl url = server.url("/fine.json");
        server.shutdown();

        SimpleTestRequest request = new SimpleTestRequest(url, account, "test");
        Result<Integer> result = request.performRequest(null);
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = RequestException.class)
    public void testResponseError() throws IOException, RequestException {
        server.enqueue(new MockResponse().setResponseCode(500));
        server.start();
        HttpUrl url = server.url("/fine.json");

        SimpleTestRequest request = new SimpleTestRequest(url, account, "test");
        Result<Integer> result = request.performRequest(null);
        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = AuthException.class)
    public void testExpiredRefreshToken() throws IOException, RequestException {
        MinervaRequest.AccessTokenProvider provider = (accountManager, account) -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, new Intent());
            return bundle;
        };
        server.start();
        HttpUrl url = server.url("/fine.json");

        SimpleTestRequest request = new SimpleTestRequest(url, account, provider);
        Result<Integer> result = request.performRequest(null);

        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test(expected = IOFailureException.class)
    public void testNullTokenInBundle() throws IOException, RequestException {
        server.start();
        HttpUrl url = server.url("/fine.json");

        SimpleTestRequest request = new SimpleTestRequest(url, account, (String) null);
        Result<Integer> result = request.performRequest(null);

        assertTrue(result.hasException());
        assertFalse(result.hasData());
        result.getOrThrow();
    }

    @Test
    public void testTokenInjection() throws IOException {
        final String expectedToken = "test-token";
        server.setDispatcher(new AuthDispatcher(expectedToken, integerJsonResponse()));
        server.start();

        HttpUrl url = server.url("/fine.json");

        SimpleTestRequest request = new SimpleTestRequest(url, account, expectedToken);
        Result<Integer> result = request.performRequest(null);

        assertTrue(result.hasData());
        assertEquals(1, (int) result.getData());
    }

    private static MockResponse integerJsonResponse() {
        return new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setBody("1");
    }

    private static class SimpleTestRequest extends MinervaRequest<Integer> {

        private final HttpUrl url;

        SimpleTestRequest(HttpUrl url, Account account, MinervaRequest.AccessTokenProvider provider) {
            super(RuntimeEnvironment.application, Integer.class, account, provider);
            this.url = url;
        }

        SimpleTestRequest(HttpUrl url, Account account, String authToken) {
            super(RuntimeEnvironment.application, Integer.class, account, (accountManager, account1) -> {
                Objects.requireNonNull(account1);
                Objects.requireNonNull(accountManager);
                Bundle b = new Bundle();
                b.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                return b;
            });
            this.url = url;
        }

        @NonNull
        @Override
        protected String getAPIUrl() {
            return url.toString();
        }
    }
}