package be.ugent.zeus.hydra.feed.cards.urgent;

import android.content.Intent;
import android.view.View;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.getShadowApplication;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class UrgentViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(activityContext, R.layout.home_card_urgent);
        UrgentViewHolder viewHolder = new UrgentViewHolder(view, adapter);
        view.performClick();

        Intent expected = new Intent(view.getContext(), MainActivity.class);
        Intent actual = getShadowApplication().getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(R.id.drawer_urgent, actual.getIntExtra(MainActivity.ARG_TAB, -1));
    }
}