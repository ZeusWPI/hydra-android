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

/**
 *  on 15/03/2016.
 */
public class DateHeaderViewHolder extends RecyclerView.ViewHolder { //todo remove duplication
    private TextView headerText;
    private SimpleDateFormat weekFormatter = new SimpleDateFormat("w", new Locale("nl"));
    private SimpleDateFormat dayFormatter = new SimpleDateFormat("cccc", new Locale("nl"));
    private DateFormat dateFormatter = SimpleDateFormat.getDateInstance();

    public DateHeaderViewHolder(View v) {
        super(v);
        headerText = (TextView) v.findViewById(R.id.resto_header_text);
    }

    public void populate(Date date) {
        headerText.setText(getFriendlyDate(date));
    }

    private String getFriendlyDate(Date date) {
        // TODO: I feel a bit bad about all of this; any good libraries?
        // I couldn't find any that were suitable -- mivdnber
        DateTime today = new DateTime();
        DateTime dateTime = new DateTime(date);
        int thisWeek = Integer.parseInt(weekFormatter.format(today.toDate()));
        int week = Integer.parseInt(weekFormatter.format(date));

        int daysBetween = Days.daysBetween(today.toLocalDate(), dateTime.toLocalDate()).getDays();

        if (daysBetween == 0) {
            return "vandaag";
        } else if(daysBetween == 1) {
            return "morgen";
        } else if(daysBetween == 2) {
            return "overmorgen";
        } else if (week == thisWeek || daysBetween < 7) {
            return dayFormatter.format(date).toLowerCase();
        } else if (week == thisWeek + 1) {
            return "volgende " + dayFormatter.format(date).toLowerCase();
        } else {
            return dateFormatter.format(date);
        }
    }
}
