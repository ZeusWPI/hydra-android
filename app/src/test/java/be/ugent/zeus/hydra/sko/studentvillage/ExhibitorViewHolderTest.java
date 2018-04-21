package be.ugent.zeus.hydra.sko.studentvillage;

import android.view.View;
import be.ugent.zeus.hydra.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
public class ExhibitorViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.item_sko_exhibitor);
        ExhibitorViewHolder viewHolder = new ExhibitorViewHolder(view);
        Exhibitor exhibitor = generate(Exhibitor.class);
        viewHolder.populate(exhibitor);

        assertTextIs(exhibitor.getName(), view.findViewById(R.id.name));
        assertNotEmpty(view.findViewById(R.id.content));
        assertTrue(view.hasOnClickListeners());
    }
}