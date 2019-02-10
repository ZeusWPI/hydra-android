package be.ugent.zeus.hydra.association.event.list;

import android.content.Context;
import androidx.annotation.VisibleForTesting;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;

import org.threeten.bp.LocalDate;

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
        return be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DateHeaderViewHolder.format(context, localDate);
    }
}