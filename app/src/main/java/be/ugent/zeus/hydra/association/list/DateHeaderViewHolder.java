package be.ugent.zeus.hydra.association.list;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.VisibleForTesting;

import java.time.LocalDate;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DataViewHolder;


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

    @VisibleForTesting
    static String formatDate(Context context, LocalDate localDate) {
        return be.ugent.zeus.hydra.common.ui.recyclerview.viewholders.DateHeaderViewHolder.format(context, localDate);
    }

    @Override
    public void populate(EventItem eventItem) {
        headerText.setText(formatDate(headerText.getContext(), eventItem.getHeader()));
    }
}