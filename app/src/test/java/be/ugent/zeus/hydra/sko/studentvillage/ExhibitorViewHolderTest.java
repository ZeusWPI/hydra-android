package be.ugent.zeus.hydra.sko.studentvillage;

import android.view.View;

import be.ugent.zeus.hydra.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.*;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertTrue;

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