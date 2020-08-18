//package be.ugent.zeus.hydra.association.list;
//
//import android.content.Intent;
//import android.view.View;
//
//import be.ugent.zeus.hydra.R;
//import be.ugent.zeus.hydra.association.event.Event;
//import be.ugent.zeus.hydra.association.event.EventDetailsActivity;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.robolectric.RobolectricTestRunner;
//import org.threeten.bp.LocalDate;
//
//import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
//import static be.ugent.zeus.hydra.testing.Utils.generate;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
///**
// * @author Niko Strijbol
// */
//@RunWith(RobolectricTestRunner.class)
//public class EventViewHolderTest {
//
//    @Test
//    public void populateMiddleEvent() {
//        testEvent(false);
//    }
//
//    @Test(expected = IllegalStateException.class)
//    public void populateHeader() {
//        View view = inflate(R.layout.item_event_item);
//        EventViewHolder viewHolder = new EventViewHolder(view);
//        EventItem item = new EventItem(generate(LocalDate.class));
//        viewHolder.populate(item);
//    }
//
//    @Test
//    public void populateLastEvent() {
//        testEvent(true);
//    }
//
//    private static void testEvent(boolean isLast) {
//        View view = inflate(R.layout.item_event_item);
//        EventViewHolder viewHolder = new EventViewHolder(view);
//        EventItem item = new EventItem(generate(Event.class), isLast);
//        Event event = item.getItem();
//        viewHolder.populate(item);
//
//        // We don't test all values, it's not worth it to just copy all code.
//        assertTextIs(event.getTitle(), view.findViewById(R.id.name));
//        assertTextIs(event.getAssociation().getDisplayName(), view.findViewById(R.id.association));
//        assertNotEmpty(view.findViewById(R.id.starttime));
//
//        // Check that the click listener works.
//        View card = view.findViewById(R.id.card_view);
//        card.performClick();
//
//        Intent expectedIntent = EventDetailsActivity.start(card.getContext(), event);
//        Intent actual = getShadowApplication().getNextStartedActivity();
//        assertEquals(expectedIntent.getComponent(), actual.getComponent());
//        assertNotNull(actual.getParcelableExtra(EventDetailsActivity.PARCEL_EVENT));
//
//        int expected = isLast ? View.GONE : View.VISIBLE;
//        assertEquals(expected, view.findViewById(R.id.item_event_divider).getVisibility());
//    }
//}
