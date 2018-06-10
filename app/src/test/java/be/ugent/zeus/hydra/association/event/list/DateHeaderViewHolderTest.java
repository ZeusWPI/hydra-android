package be.ugent.zeus.hydra.association.event.list;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class DateHeaderViewHolderTest {

    @Test
    public void populate() {
        View view = inflate(R.layout.item_date_header);
        DateHeaderViewHolder viewHolder = new DateHeaderViewHolder(view);
        EventItem item = generate(EventItem.class);

        Context c = RuntimeEnvironment.application;
        String expectedDate = DateHeaderViewHolder.formatDate(c, item.getHeader());

        viewHolder.populate(item);
        TextView textView = view.findViewById(R.id.date_header);

        assertEquals(expectedDate, textView.getText());
    }
}