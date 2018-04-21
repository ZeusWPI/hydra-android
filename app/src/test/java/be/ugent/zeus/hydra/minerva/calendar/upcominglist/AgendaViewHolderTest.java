package be.ugent.zeus.hydra.minerva.calendar.upcominglist;

import android.content.Intent;
import android.provider.CalendarContract;
import android.view.View;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.minerva.calendar.AgendaItem;
import be.ugent.zeus.hydra.minerva.calendar.itemdetails.AgendaActivity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertNotEmpty;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class AgendaViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.item_minerva_agenda);
        AgendaItem item = generate(AgendaItem.class);
        AgendaViewHolder viewHolder = new AgendaViewHolder(view);
        viewHolder.populate(item);

        assertTextIs(item.getTitle(), view.findViewById(R.id.title));
        assertNotEmpty(view.findViewById(R.id.subtitle));
        view.findViewById(R.id.parent_layout).performClick();

        Intent expected = new Intent(view.getContext(), AgendaActivity.class);
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();

        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(item.getUri(), actual.getStringExtra(CalendarContract.Events.CUSTOM_APP_URI));
    }
}