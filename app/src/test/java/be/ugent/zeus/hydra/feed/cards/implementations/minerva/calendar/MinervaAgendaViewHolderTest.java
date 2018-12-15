package be.ugent.zeus.hydra.feed.cards.implementations.minerva.calendar;

import android.content.Intent;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import be.ugent.zeus.hydra.minerva.calendar.itemdetails.AgendaActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.threeten.bp.LocalDate;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class MinervaAgendaViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(activityContext, R.layout.home_minerva_agenda_card);
        MinervaAgendaViewHolder viewHolder = new MinervaAgendaViewHolder(view, adapter);
        LocalDate date = LocalDate.now();
        List<AgendaItem> items = generate(AgendaItem.class, 10).collect(Collectors.toList());
        MinervaAgendaCard card = new MinervaAgendaCard(date, items);
        viewHolder.populate(card);

        LinearLayout layout = view.findViewById(R.id.linear_layout);

        assertEquals(10, layout.getChildCount());

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            AgendaItem item = items.get(i);
            assertTextIs(item.getTitle(), child.findViewById(R.id.title));
            assertNotEmpty(child.findViewById(R.id.subtitle));

            child.performClick();
            Intent expected = new Intent(child.getContext(), AgendaActivity.class);
            Intent actual = getShadowApplication().getNextStartedActivity();
            assertEquals(expected.getComponent(), actual.getComponent());

            assertEquals(item.getUri(), actual.getStringExtra(CalendarContract.Events.CUSTOM_APP_URI));
        }
    }
}