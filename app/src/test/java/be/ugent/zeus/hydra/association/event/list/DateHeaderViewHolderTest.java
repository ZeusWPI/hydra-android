package be.ugent.zeus.hydra.association.event.list;

import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.threeten.bp.format.FormatStyle;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.inflate;
import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

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

        String expectedDate = viewHolder.formatDate(item.getHeader());

        viewHolder.populate(item);
        TextView textView = view.findViewById(R.id.date_header);

        assertEquals(expectedDate, textView.getText());
    }
}