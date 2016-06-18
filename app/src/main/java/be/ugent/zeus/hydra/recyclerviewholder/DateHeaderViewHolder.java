package be.ugent.zeus.hydra.recyclerviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.DateUtils;

/**
 *  on 15/03/2016.
 */
public class DateHeaderViewHolder extends RecyclerView.ViewHolder { //todo remove duplication
    private TextView headerText;

    public DateHeaderViewHolder(View v) {
        super(v);
        headerText = (TextView) v.findViewById(R.id.resto_header_text);
    }

    public void populate(Date date) {
        headerText.setText(DateUtils.getFriendlyDate(date));
    }
}
