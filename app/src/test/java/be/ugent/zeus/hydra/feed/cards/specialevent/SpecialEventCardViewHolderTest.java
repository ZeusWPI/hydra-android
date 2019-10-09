package be.ugent.zeus.hydra.feed.cards.specialevent;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import be.ugent.zeus.hydra.specialevent.SpecialEvent;
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
public class SpecialEventCardViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(activityContext, R.layout.home_card_special);
        SpecialEventCardViewHolder cardViewHolder = new SpecialEventCardViewHolder(view, adapter.getCompanion());
        SpecialEventCard card = generate(SpecialEventCard.class);
        SpecialEvent event = card.getSpecialEvent();
        event.setInApp("test");
        cardViewHolder.populate(card);

        assertTextIs(event.getName(), view.findViewById(R.id.title));
        assertTextIs(event.getSimpleText(), view.findViewById(R.id.text));

        view.performClick();
        Intent expected = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getLink()));
        Intent actual = getShadowApplication().getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
    }
}