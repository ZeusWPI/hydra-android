package be.ugent.zeus.hydra.association.event.list;

import android.content.Context;
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

    private final TextView headerText;

    DateHeaderViewHolder(View v) {
        super(v);
        headerText = v.findViewById(R.id.date_header);
    }

    @Override
    public void populate(EventItem eventItem) {
        headerText.setText(formatDate(headerText.getContext(), eventItem.getHeader()));
    }

    @VisibleForTesting
    static String formatDate(Context context, LocalDate localDate) {
        String date = DateUtils.getFriendlyDate(context, localDate, FormatStyle.LONG);
        date = date.substring(0, 1).toUpperCase() + date.substring(1);
        return date;
    }
}