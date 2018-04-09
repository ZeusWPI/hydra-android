package be.ugent.zeus.hydra.feed.cards.implementations.event;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.EventDetailsActivity;
import be.ugent.zeus.hydra.feed.HomeFeedAdapter;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import be.ugent.zeus.hydra.feed.preferences.HomeFragment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowApplication;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class EventCardViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.home_card_event);
        EventCardViewHolder viewHolder = new EventCardViewHolder(view, adapter);
        EventCard eventCard = generate(EventCard.class);
        Event event = eventCard.getEvent();
        viewHolder.populate(eventCard);

        assertTextIs(event.getTitle(), view.findViewById(R.id.name));
        assertTextIs(event.getLocation(), view.findViewById(R.id.association));
        assertNotEmpty(view.findViewById(R.id.starttime));

        view.performClick();
        Intent expected = EventDetailsActivity.start(RuntimeEnvironment.application, event);
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
    }

}