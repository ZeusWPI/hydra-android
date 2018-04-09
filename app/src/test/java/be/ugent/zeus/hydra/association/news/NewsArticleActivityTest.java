package be.ugent.zeus.hydra.association.news;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import be.ugent.zeus.hydra.common.RobolectricTest;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class NewsArticleActivityTest {

    private ShadowConnectivityManager shadow;

    @Before
    public void setUp() {
        ConnectivityManager connectivityManager = RuntimeEnvironment.application.getSystemService(ConnectivityManager.class);
        shadow = shadowOf(connectivityManager);
    }

    private void setUseCustomTabs(boolean use) {
        PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application)
                .edit()
                .putBoolean(ArticlePreferenceFragment.PREF_USE_CUSTOM_TABS, use)
                .commit();
    }

    private void assertIntent(UgentNewsItem item) {
        Intent expected = new Intent(RuntimeEnvironment.application, NewsArticleActivity.class);
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(item, actual.getParcelableExtra(NewsArticleActivity.PARCEL_NAME));
    }

    @Test
    public void viewArticleCustomTabsOffline() {
        ActivityHelper helper = mock(ActivityHelper.class);
        UgentNewsItem newsItem = generate(UgentNewsItem.class);
        shadow.setActiveNetworkInfo(ShadowNetworkInfo.newInstance(
                null,
                ConnectivityManager.TYPE_WIFI,
                TelephonyManager.NETWORK_TYPE_IWLAN,
                true,
                NetworkInfo.State.DISCONNECTED));
        shadow.setDefaultNetworkActive(true);
        setUseCustomTabs(true);

        NewsArticleActivity.viewArticle(RuntimeEnvironment.application, newsItem, helper);
        assertIntent(newsItem);
    }

    @Test
    public void viewArticleCustomTabsOnline() {
        ActivityHelper helper = mock(ActivityHelper.class);
        UgentNewsItem newsItem = generate(UgentNewsItem.class);
        shadow.setActiveNetworkInfo(ShadowNetworkInfo.newInstance(
                null,
                ConnectivityManager.TYPE_WIFI,
                TelephonyManager.NETWORK_TYPE_IWLAN,
                true,
                NetworkInfo.State.CONNECTED));
        shadow.setDefaultNetworkActive(true);
        setUseCustomTabs(true);

        NewsArticleActivity.viewArticle(RuntimeEnvironment.application, newsItem, helper);
        verify(helper, times(1)).openCustomTab(any(Uri.class));
    }

    @Test
    public void viewArticleNoCustomTabs() {
        ActivityHelper helper = mock(ActivityHelper.class);
        UgentNewsItem newsItem = generate(UgentNewsItem.class);
        setUseCustomTabs(false);

        NewsArticleActivity.viewArticle(RuntimeEnvironment.application, newsItem, helper);
        assertIntent(newsItem);
    }
}