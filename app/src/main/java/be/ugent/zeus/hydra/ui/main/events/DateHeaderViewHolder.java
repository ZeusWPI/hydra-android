package be.ugent.zeus.hydra.ui.main.events;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.format.FormatStyle;

/**
 * For date headers.
 *
 * @author Niko Strijbol
 * @author unknown
 */
class DateHeaderViewHolder extends DataViewHolder<EventItem> {

    private TextView headerText;

    DateHeaderViewHolder(View v) {
        super(v);
        headerText = v.findViewById(R.id.date_header);
    }

    public void populate(EventItem eventItem) {
        String date = DateUtils.getFriendlyDate(eventItem.getHeader(), FormatStyle.LONG);
        date = date.substring(0, 1).toUpperCase() + date.substring(1);
        headerText.setText(date);
    }
}