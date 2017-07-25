package be.ugent.zeus.hydra.ui.common.recyclerview.viewholders;

import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.ZonedDateTime;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * For date headers.
 *
 * @author Niko Strijbol
 * @author unknown
 */
public class DateHeaderViewHolder extends DataViewHolder<ZonedDateTime> {

    private TextView headerText;

    public DateHeaderViewHolder(View v) {
        super(v);
        headerText = $(v, R.id.date_header);
    }

    public void populate(ZonedDateTime date) {
        headerText.setText(DateUtils.getFriendlyDate(date.toLocalDate()));
    }
}