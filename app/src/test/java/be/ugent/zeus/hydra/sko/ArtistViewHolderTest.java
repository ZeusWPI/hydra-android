package be.ugent.zeus.hydra.sko;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.sko.Artist;
import be.ugent.zeus.hydra.sko.ArtistDetailsActivity;
import be.ugent.zeus.hydra.sko.ArtistOrTitle;
import be.ugent.zeus.hydra.sko.ArtistViewHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class ArtistViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.item_sko_lineup_artist);
        ArtistViewHolder viewHolder = new ArtistViewHolder(view);
        Artist artist = generate(Artist.class);
        viewHolder.populate(new ArtistOrTitle(artist));

        assertTextIs(artist.getName(), view.findViewById(R.id.title));
        assertTextIs(artist.getDisplayDate(view.getContext()), view.findViewById(R.id.date));

        ShadowApplication application = getShadowApplication();
        view.performClick();
        Intent expected = ArtistDetailsActivity.start(view.getContext(), artist);
        Intent actual = application.getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());

        MenuItem item = mock(MenuItem.class);
        when(item.getItemId()).thenReturn(ArtistViewHolder.MENU_ID_ADD_TO_CALENDAR);
        viewHolder.onMenuItemClick(item);

        expected = artist.addToCalendarIntent();
        actual = application.getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
    }
}
