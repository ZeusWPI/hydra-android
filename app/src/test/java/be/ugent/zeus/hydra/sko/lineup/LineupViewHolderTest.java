package be.ugent.zeus.hydra.sko.lineup;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import be.ugent.zeus.hydra.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class LineupViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.item_sko_lineup);
        LineupViewHolder viewHolder = new LineupViewHolder(view);
        Artist artist = generate(Artist.class);
        viewHolder.populate(artist);

        assertTextIs(artist.getName(), view.findViewById(R.id.title));
        assertTextIs(artist.getDisplayDate(view.getContext()), view.findViewById(R.id.date));

        view.performClick();
        Intent expected = ArtistDetailsActivity.start(view.getContext(), artist);
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());

        MenuItem item = mock(MenuItem.class);
        when(item.getItemId()).thenReturn(LineupViewHolder.MENU_ID_ADD_TO_CALENDAR);
        viewHolder.onMenuItemClick(item);

        expected = artist.addToCalendarIntent();
        actual = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
    }
}