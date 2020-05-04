package be.ugent.zeus.hydra.feed.cards.resto;

import android.content.Intent;
import android.view.View;

import be.ugent.zeus.hydra.MainActivity;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.widgets.MenuTable;
import be.ugent.zeus.hydra.feed.cards.implementations.AbstractFeedViewHolderTest;
import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.resto.menu.RestoFragment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.getShadowApplication;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class RestoCardViewHolderTest extends AbstractFeedViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(activityContext, R.layout.home_card_resto);
        RestoCardViewHolder viewHolder = new RestoCardViewHolder(view, adapter);
        RestoMenuCard card = generate(RestoMenuCard.class);
        viewHolder.populate(card);

        MenuTable table = view.findViewById(R.id.menu_table);
        RestoMenu actualMenu = table.getMenu();

        assertEquals(card.getRestoMenu(), actualMenu);

        view.performClick();

        Intent expected = new Intent(view.getContext(), MainActivity.class);
        Intent actual = getShadowApplication().getNextStartedActivity();
        assertEquals(expected.getComponent(), actual.getComponent());
        assertEquals(actualMenu.getDate(), actual.getSerializableExtra(RestoFragment.ARG_DATE));
        assertEquals(R.id.drawer_resto, actual.getIntExtra(MainActivity.ARG_TAB, -1));
    }
}