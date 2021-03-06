package be.ugent.zeus.hydra.feed.cards.event;

import android.content.Intent;
import android.util.Pair;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.association.Association;
import be.ugent.zeus.hydra.association.event.Event;
import be.ugent.zeus.hydra.association.event.EventDetailsActivity;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import org.junit.Ignore;
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
@Ignore
public class EventCardViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(activityContext, R.layout.home_card_event);
        EventCardViewHolder viewHolder = new EventCardViewHolder(view, adapter);
        EventCard eventCard = generate(EventCard.class);
        Pair<Event, Association> event = eventCard.getEvent();
        viewHolder.populate(eventCard);

        assertTextIs(event.first.getTitle(), view.findViewById(R.id.name));
        assertTextIs(event.first.getLocation(), view.findViewById(R.id.association));
        assertNotEmpty(view.findViewById(R.id.starttime));

        view.performClick();
        Intent expected = EventDetailsActivity.start(activityContext, event.first, event.second);
        Intent actual = getShadowApplication().getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
    }

}