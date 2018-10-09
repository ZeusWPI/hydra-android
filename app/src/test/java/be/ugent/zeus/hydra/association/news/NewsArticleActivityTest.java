package be.ugent.zeus.hydra.association.news;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import androidx.test.core.app.ApplicationProvider;

import be.ugent.zeus.hydra.common.article.CustomTabPreferenceFragment;
import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.testing.RobolectricUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.getShadowApplication;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.robolectric.Shadows.shadowOf;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class NewsArticleActivityTest {

    private ShadowConnectivityManager shadow;

    @Before
    public void setUp() {
        ConnectivityManager connectivityManager = ApplicationProvider.getApplicationContext()
                .getSystemService(ConnectivityManager.class);
        shadow = shadowOf(connectivityManager);
    }

    private static void setUseCustomTabs(boolean use) {
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
                .edit()
                .putBoolean(CustomTabPreferenceFragment.PREF_USE_CUSTOM_TABS, use)
                .commit();
    }

    private static void assertIntent(Context context, UgentNewsItem item) {
        Intent expected = new Intent(context, NewsArticleActivity.class);
        Intent actual = getShadowApplication().getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(item, actual.getParcelableExtra(NewsArticleActivity.PARCEL_NAME));
    }

    @Test
    @SuppressWarnings("deprecation")
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

        Context context = RobolectricUtils.getActivityContext();
        NewsArticleActivity.viewArticle(context, newsItem, helper);
        assertIntent(context, newsItem);
    }

    @Test
    @SuppressWarnings("deprecation")
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

        NewsArticleActivity.viewArticle(RobolectricUtils.getActivityContext(), newsItem, helper);
        verify(helper, times(1)).openCustomTab(any(Uri.class));
    }

    @Test
    public void viewArticleNoCustomTabs() {
        ActivityHelper helper = mock(ActivityHelper.class);
        UgentNewsItem newsItem = generate(UgentNewsItem.class);
        setUseCustomTabs(false);

        Context context = RobolectricUtils.getActivityContext();
        NewsArticleActivity.viewArticle(context, newsItem, helper);
        assertIntent(context, newsItem);
    }
}