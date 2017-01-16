package be.ugent.zeus.hydra.homefeed.content.minerva.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.minerva.AgendaActivity;
import be.ugent.zeus.hydra.homefeed.HomeFeedAdapter;
import be.ugent.zeus.hydra.homefeed.content.HideableViewHolder;
import be.ugent.zeus.hydra.homefeed.content.HomeCard;
import be.ugent.zeus.hydra.models.minerva.AgendaItem;
import be.ugent.zeus.hydra.utils.DateUtils;
import org.threeten.bp.LocalDateTime;
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

    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", new Locale("nl"));

    private final LinearLayout layout;

    public MinervaAgendaViewHolder(View v, HomeFeedAdapter adapter) {
        super(v, adapter);
        layout = $(v, R.id.linear_layout);
    }

    @Override
    public void populate(HomeCard card) {
        super.populate(card);

        final MinervaAgendaCard mCard = card.checkCard(HomeCard.CardType.MINERVA_AGENDA);

        String titleS = itemView.getContext().getString(R.string.home_feed_agenda_title, DateUtils.getFriendlyDate(mCard.getDate()));
        toolbar.setTitle(titleS);

        layout.removeAllViewsInLayout();

        for (final AgendaItem item : mCard.getAgendaItems()) {

            View view = LayoutInflater.from(layout.getContext()).inflate(R.layout.item_minerva_home_announcement, layout, false);
            TextView title = $(view, R.id.title);
            TextView subtitle = $(view, R.id.subtitle);

            title.setText(item.getTitle());
            subtitle.setText(relativeTimeSpan(view.getContext(), item.getStartDate(), item.getEndDate()) + " (" + item.getCourse().getTitle() + ")");

            layout.addView(view);

            view.setOnClickListener(v -> AgendaActivity.start(v.getContext(), item));
        }
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

        LocalDateTime localStart = DateUtils.toLocalDateTime(start);
        LocalDateTime localEnd = DateUtils.toLocalDateTime(end);

        if(start.isBefore(now) && end.isAfter(now)) {
            String endString;
            if(android.text.format.DateUtils.isToday(end.toInstant().toEpochMilli())) {
                endString = localEnd.format(HOUR_FORMATTER);
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
            return localStart.format(HOUR_FORMATTER) + " tot " + localEnd.format(HOUR_FORMATTER);
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