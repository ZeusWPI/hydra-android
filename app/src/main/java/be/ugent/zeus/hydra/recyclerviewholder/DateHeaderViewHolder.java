package be.ugent.zeus.hydra.recyclerviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.DateUtils;

import java.util.Date;

/**
 * For date headers.
 *
 * @author Niko Strijbol
 * @author unknown
 */
public class DateHeaderViewHolder extends RecyclerView.ViewHolder {
    private TextView headerText;

    public DateHeaderViewHolder(View v) {
        super(v);
        headerText = (TextView) v.findViewById(R.id.date_header);
    }

    public void populate(Date date) {
        headerText.setText(DateUtils.getFriendlyDate(date));
    }
}
