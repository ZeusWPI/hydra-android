package be.ugent.zeus.hydra.common.ui.recyclerview.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.FormatStyle;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import be.ugent.zeus.hydra.common.utils.StringUtils;

/**
 * For date headers.
 *
 * @author Niko Strijbol
 */
public class DateHeaderViewHolder extends DataViewHolder<OffsetDateTime> {

    private final TextView headerText;

    public DateHeaderViewHolder(View v) {
        super(v);
        headerText = v.findViewById(R.id.date_header);
    }

    public static String format(Context context, LocalDate localDate) {
        String date = DateUtils.getFriendlyDate(context, localDate, FormatStyle.LONG);
        date = StringUtils.capitaliseFirst(date);
        return date;
    }

    @Override
    public void populate(OffsetDateTime date) {
        headerText.setText(format(headerText.getContext(), date.toLocalDate()));
    }
}
