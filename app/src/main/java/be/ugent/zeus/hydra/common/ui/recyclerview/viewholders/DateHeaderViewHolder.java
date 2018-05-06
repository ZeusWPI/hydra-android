package be.ugent.zeus.hydra.common.ui.recyclerview.viewholders;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.FormatStyle;

/**
 * For date headers.
 *
 * @author Niko Strijbol
 * @author unknown
 */
public class DateHeaderViewHolder extends DataViewHolder<OffsetDateTime> {

    private final TextView headerText;

    public DateHeaderViewHolder(View v) {
        super(v);
        headerText = v.findViewById(R.id.date_header);
    }

    @Override
    public void populate(OffsetDateTime date) {
        headerText.setText(DateUtils.getFriendlyDate(date.toLocalDate(), FormatStyle.LONG));
    }
}