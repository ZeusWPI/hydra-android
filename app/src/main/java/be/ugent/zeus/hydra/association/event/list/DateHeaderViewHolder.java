package be.ugent.zeus.hydra.association.event.list;

import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.FormatStyle;

/**
 * For date headers.
 *
 * @author Niko Strijbol
 */
class DateHeaderViewHolder extends DataViewHolder<EventItem> {

    private TextView headerText;

    DateHeaderViewHolder(View v) {
        super(v);
        headerText = v.findViewById(R.id.date_header);
    }

    public void populate(EventItem eventItem) {
        headerText.setText(formatDate(eventItem.getHeader()));
    }

    @VisibleForTesting
    String formatDate(LocalDate localDate) {
        String date = DateUtils.getFriendlyDate(localDate, FormatStyle.LONG);
        date = date.substring(0, 1).toUpperCase() + date.substring(1);
        return date;
    }
}