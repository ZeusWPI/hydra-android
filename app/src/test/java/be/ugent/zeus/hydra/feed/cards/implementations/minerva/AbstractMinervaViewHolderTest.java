package be.ugent.zeus.hydra.feed.cards.implementations.minerva;

import android.content.Intent;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class AbstractMinervaViewHolderTest {

    protected HomeFeedAdapter adapter;

    @Before
    public void setUp() {
        adapter = mock(HomeFeedAdapter.class);
        HomeFeedAdapter.AdapterCompanion companion = mock(HomeFeedAdapter.AdapterCompanion.class);
        when(adapter.getCompanion()).thenReturn(companion);
        when(companion.getContext()).thenReturn(RuntimeEnvironment.application);
        doAnswer((Answer<Void>) invocation -> {
            RuntimeEnvironment.application.startActivity(invocation.getArgument(0));
            return null;
        }).when(companion).startActivityForResult(any(Intent.class), anyInt());
    }
}
