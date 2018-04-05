package be.ugent.zeus.hydra.feed.cards.implementations.urgent;

import android.content.Intent;
import android.view.View;
import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class UrgentViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.home_card_urgent);
        UrgentViewHolder viewHolder = new UrgentViewHolder(view, adapter);
        view.performClick();

        Intent expected = new Intent(view.getContext(), MainActivity.class);
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(R.id.drawer_urgent, actual.getIntExtra(MainActivity.ARG_TAB, -1));
    }
}