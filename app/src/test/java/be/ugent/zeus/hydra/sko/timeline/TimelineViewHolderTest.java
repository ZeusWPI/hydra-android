package be.ugent.zeus.hydra.sko.timeline;

import android.view.View;
import be.ugent.zeus.hydra.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.assertTextIs;
import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class TimelineViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.item_sko_timeline_post);
        TimelineAdapter adapter = mock(TimelineAdapter.class);
        TimelineViewHolder viewHolder = new TimelineViewHolder(view, adapter);

        TimelinePost post = generate(TimelinePost.class);
        viewHolder.populate(post);

        assertTextIs(post.getTitle(), view.findViewById(R.id.post_title));
        assertTrue(view.hasOnClickListeners());
    }
}