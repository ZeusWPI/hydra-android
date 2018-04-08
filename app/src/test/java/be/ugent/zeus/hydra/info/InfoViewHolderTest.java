package be.ugent.zeus.hydra.info;

import android.view.View;
import be.ugent.zeus.hydra.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class InfoViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.info_card);
        // Let the image field be null to prevent errors in later tests.
        InfoItem infoItem = generate(InfoItem.class, "image");
        InfoViewHolder holder = new InfoViewHolder(view);
        holder.populate(infoItem);

        assertTextIs(infoItem.getTitle(), view.findViewById(R.id.info_name));
        assertTrue(view.hasOnClickListeners());
    }
}