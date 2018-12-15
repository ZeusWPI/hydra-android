package be.ugent.zeus.hydra.feed.cards.implementations.event;

import android.content.Intent;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.EventDetailsActivity;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class EventCardViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(activityContext, R.layout.home_card_event);
        EventCardViewHolder viewHolder = new EventCardViewHolder(view, adapter);
        EventCard eventCard = generate(EventCard.class);
        Event event = eventCard.getEvent();
        viewHolder.populate(eventCard);

        assertTextIs(event.getTitle(), view.findViewById(R.id.name));
        assertTextIs(event.getLocation(), view.findViewById(R.id.association));
        assertNotEmpty(view.findViewById(R.id.starttime));

        view.performClick();
        Intent expected = EventDetailsActivity.start(activityContext, event);
        Intent actual = getShadowApplication().getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
    }

}