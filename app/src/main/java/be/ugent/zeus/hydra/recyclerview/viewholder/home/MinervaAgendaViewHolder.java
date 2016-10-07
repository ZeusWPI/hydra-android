package be.ugent.zeus.hydra.recyclerview.viewholder.home;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.AgendaActivity;
import be.ugent.zeus.hydra.models.cards.HomeCard;
import be.ugent.zeus.hydra.models.cards.MinervaAgendaCard;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.recyclerview.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

/**
 * Minerva agenda item.
 *
 * @author Niko Strijbol
 */
public class MinervaAgendaViewHolder extends HideableViewHolder {

    private static final DateTimeFormatter START_FORMATTER = DateTimeFormatter.ofPattern("dd/MM - HH:mm", new Locale("nl"));
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", new Locale("nl"));

    private TextView date;
    private TextView location;
    private TextView title;

    public MinervaAgendaViewHolder(View v, HomeCardAdapter adapter) {
        super(v, adapter);

        date = $(v, R.id.date);
        location = $(v, R.id.location);
        title = $(v, R.id.title);
    }

    @Override
    public void populate(HomeCard card) {

        final MinervaAgendaCard mCard = card.checkCard(HomeCard.CardType.MINERVA_AGENDA);
        final AgendaItem item = mCard.getAgendaItem();

        toolbar.setTitle("Activiteit van " + item.getCourse().getTitle());

        title.setText(item.getTitle());
        date.setText(relativeTimeSpan(itemView.getContext(), item.getStartDate(), item.getEndDate()));

        if (TextUtils.isEmpty(item.getLocation())) {
            location.setVisibility(View.GONE);
        } else {
            location.setVisibility(View.VISIBLE);
            location.setText(item.getLocation());
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set onclick listener
                AgendaActivity.start(itemView.getContext(), mCard.getAgendaItem());
            }
        });

        super.populate(card);
    }

    /**
     * Get a relative date string for a start and stop date. The string accounts for events that are on the same day,
     * by not showing the day twice for example.
     *
     * @param start The start date. Should be before the end date.
     * @param end The end date.
     * @return The string.
     */
    private static String relativeTimeSpan(Context context, ZonedDateTime start, ZonedDateTime end) {

        ZonedDateTime now = ZonedDateTime.now();
        if(start.isBefore(now) && end.isAfter(now)) {
            String endString = "";
            if(android.text.format.DateUtils.isToday(end.toInstant().toEpochMilli())) {
                endString = end.format(HOUR_FORMATTER);
            } else {
                endString = android.text.format.DateUtils.formatDateTime(
                        context,
                        end.toInstant().toEpochMilli(),
                        android.text.format.DateUtils.FORMAT_SHOW_DATE | android.text.format.DateUtils.FORMAT_SHOW_TIME
                );
            }

            return "Nu tot " + endString;
        }


        if(start.getDayOfMonth() == end.getDayOfMonth()) {
            String append = "";
            if(DateUtils.isThisWeek(start.toLocalDate())) {
                append = " (" + DateUtils.getFriendlyDate(start.toLocalDate()) + ")";
            }
            return DateUtils.relativeDateTimeString(start, context) + " tot " + end.format(HOUR_FORMATTER) + append;
        } else {
            return android.text.format.DateUtils.formatDateRange(
                    context,
                    start.toInstant().toEpochMilli(),
                    end.toInstant().toEpochMilli(),
                    android.text.format.DateUtils.FORMAT_SHOW_DATE | android.text.format.DateUtils.FORMAT_SHOW_TIME
            );
        }
    }
}