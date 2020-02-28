package be.ugent.zeus.hydra.common.ui.recyclerview.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.common.utils.DateUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.FormatStyle;

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

    @Override
    public void populate(OffsetDateTime date) {
        headerText.setText(format(headerText.getContext(), date.toLocalDate()));
    }

    public static String format(Context context, LocalDate localDate) {
        String date = DateUtils.getFriendlyDate(context, localDate, FormatStyle.LONG);
        date = date.substring(0, 1).toUpperCase() + date.substring(1);
        return date;
    }
}
