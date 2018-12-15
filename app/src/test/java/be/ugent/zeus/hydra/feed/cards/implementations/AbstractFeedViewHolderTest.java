package be.ugent.zeus.hydra.feed.cards.implementations;

import android.content.Context;
import android.content.Intent;

import be.ugent.zeus.hydra.common.ui.customtabs.ActivityHelper;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.testing.RobolectricUtils;
import org.junit.Before;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * @author Niko Strijbol
 */
public class AbstractFeedViewHolderTest {

    protected HomeFeedAdapter adapter;
    protected ActivityHelper helper;
    protected Context activityContext;

    @Before
    public void setUp() {
        adapter = mock(HomeFeedAdapter.class);
        HomeFeedAdapter.AdapterCompanion companion = mock(HomeFeedAdapter.AdapterCompanion.class);
        when(adapter.getCompanion()).thenReturn(companion);
        activityContext = RobolectricUtils.getActivityContext();
        when(companion.getContext()).thenReturn(activityContext);
        doAnswer((Answer<Void>) invocation -> {
            activityContext.startActivity(invocation.getArgument(0));
            return null;
        }).when(companion).startActivityForResult(any(Intent.class), anyInt());
        helper = mock(ActivityHelper.class);
        when(companion.getHelper()).thenReturn(helper);
    }
}